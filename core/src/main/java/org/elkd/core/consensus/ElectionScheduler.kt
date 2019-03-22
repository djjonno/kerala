package org.elkd.core.consensus

import org.apache.log4j.Logger
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.server.cluster.ClusterMessenger

/**
 * Performs an election across the cluster, requesting votes and tallying responses.
 *
 * @param onSuccess If election was successful, this Runnable will be executed
 * @param onFailure Opposite to onSuccess, this Runnable is called when election failed.
 * @param clusterMessenger - Messenger mechanism, contains logical cluster to perform election across.
 */
class ElectionScheduler private constructor(private val voteRequest: RequestVoteRequest,
                                            private var onSuccess: Runnable?,
                                            private var onFailure: Runnable?,
                                            private val clusterMessenger: ClusterMessenger) {
  private var scheduled = false
  private var electionTally: ElectionTally = ElectionTally(
      voteRequest,
      clusterMessenger.clusterSet.size(),
      ElectionType.MAJORITY,
      onSuccessDecorator(onSuccess),
      onFailureDecorator(onFailure))

  fun schedule() {
    if (scheduled) return
    LOG.info("scheduling a new election with $voteRequest")
    scheduled = true

    /* Vote for self */
    electionTally.recordVote(clusterMessenger.clusterSet.selfNode)
  }

  fun cancel() {
    onSuccess = null
    onFailure = null
  }

  fun onSuccessDecorator(runnable: Runnable?): Runnable? {
    return Runnable {
      LOG.info("election was successful $electionTally")
      runnable?.run()
    }
  }

  fun onFailureDecorator(runnable: Runnable?): Runnable? {
    return Runnable {
      LOG.info("election was not successful $electionTally")
      runnable?.run()
    }
  }

  companion object {
    private val LOG = Logger.getLogger(ElectionScheduler::class.java.name)
    @JvmStatic fun create(voteRequest: RequestVoteRequest,
                          onSuccess: Runnable?,
                          onFailure: Runnable?,
                          clusterMessenger: ClusterMessenger): ElectionScheduler {
      return ElectionScheduler(voteRequest, onSuccess, onFailure, clusterMessenger)
    }
  }
}
