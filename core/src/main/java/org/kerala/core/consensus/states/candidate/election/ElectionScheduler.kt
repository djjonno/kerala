package org.kerala.core.consensus.states.candidate.election

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.log4j.Logger
import org.kerala.core.consensus.messages.RequestVoteRequest
import org.kerala.core.server.cluster.ClusterMessenger
import org.kerala.core.server.cluster.Node

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
    private var onSuccess: () -> Unit,
    private var onFailure: () -> Unit,
    private val clusterMessenger: ClusterMessenger
) : CoroutineScope {
  val job: Job
    get() = Job()
  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.IO

  private var scheduled = false
  private var finished = false
  private var electionTally: ElectionTally = ElectionTally(clusterMessenger.clusterSet.allNodes.size)

  fun schedule() {
    if (scheduled) return

    scheduled = true

    /* Vote for self */
    handleVoteResponse(clusterMessenger.clusterSet.selfNode, true)

    /* dispatch votes across cluster */
    launch(coroutineContext) { dispatchVoteRequest() }
  }

  fun finish() {
    finished = true
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
          LOGGER.info("election won")
          onSuccess()
        }
        false -> {
          LOGGER.info("election lost")
          onFailure()
        }
      }
      finish()
    }
  }

  companion object {
    private val LOGGER = Logger.getLogger(ElectionScheduler::class.java.name)
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
          onSuccess,
          onFailure,
          clusterMessenger
      )
    }
  }
}
