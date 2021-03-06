package org.kerala.core.consensus.states.candidate.election

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kerala.core.concurrency.Pools
import org.kerala.core.concurrency.asCoroutineScope
import org.kerala.core.consensus.messages.RequestVoteRequest
import org.kerala.core.server.cluster.ClusterMessenger
import org.kerala.core.server.cluster.Node
import java.util.concurrent.ExecutorService

/**
 * Performs an election across the cluster, requesting votes and tallying responses.
 *
 * @param voteRequest The command to use to command votes from cluster.
 * @param electionStrategy The election strategy to use to count the votes and determine outcome.
 * @param onSuccess Election success callback
 * @param onFailure Election fail callback
 * @param clusterMessenger Messenger mechanism, contains logical cluster to perform election across.
 */
class ElectionScheduler private constructor(
    private val voteRequest: RequestVoteRequest,
    private val electionStrategy: ElectionStrategy,
    private val clusterMessenger: ClusterMessenger,
    private var onSuccess: () -> Unit,
    private var onFailure: () -> Unit,
    private var job: Job = Job(),
    private var threadPool: ExecutorService = Pools.consensusPool
) : CoroutineScope by threadPool.asCoroutineScope(job) {

  private var scheduled = false
  private var finished = false
  private var electionTally: ElectionTally = ElectionTally(clusterMessenger.clusterSet.allNodes.size)

  fun schedule() {
    if (scheduled) return

    scheduled = true

    /* Vote for self */
    handleVoteResponse(clusterMessenger.clusterSet.selfNode, true)

    /* dispatch votes across cluster */
    launch { dispatchVoteRequest() }
  }

  fun finish() {
    finished = true
    job.cancel()
  }

  private suspend fun dispatchVoteRequest() {
    clusterMessenger.clusterSet.nodes.forEach {
      clusterMessenger.dispatchRequestVote(it, voteRequest, onSuccess = { response ->
        handleVoteResponse(it, response.isVoteGranted)
      })
    }
  }

  private fun handleVoteResponse(node: Node, isVoteGranted: Boolean?) {
    try {
      when (isVoteGranted) {
        true -> electionTally.recordUpVote(node.id)
        false -> electionTally.recordDownVote(node.id)
      }
      postVoteCheck()
    } catch (e: Exception) { }
  }

  private fun postVoteCheck() {
    if (!finished && electionStrategy.isComplete(electionTally)) {
      when (electionStrategy.isSuccessful(electionTally)) {
        true -> {
          onSuccess()
        }
        false -> {
          onFailure()
        }
      }
      finish()
    }
  }

  companion object {
    @JvmStatic fun create(
        voteRequest: RequestVoteRequest,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        clusterMessenger: ClusterMessenger
    ): ElectionScheduler {
      return ElectionScheduler(
          voteRequest,
          /* Raft uses majority, so we'll just hard-code this strategy for now. */
          MajorityElectionStrategy(),
          clusterMessenger,
          onSuccess,
          onFailure
      )
    }
  }
}
