package org.elkd.core.consensus.replication

import kotlinx.coroutines.delay
import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.consensus.LeaderContext
import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.server.cluster.Node
import org.elkd.shared.annotations.Mockable
import kotlin.math.max
import kotlin.system.measureTimeMillis

/**
 * ReplicationController replicates the given raft state to the target {$link Node}.
 *
 * @see Node
 * @see Replicator
 */
@Mockable
class ReplicationController(val target: Node,
                            val leaderContext: LeaderContext,
                            val raft: Raft,
                            private val replicatorStrategy: ReplicatorStrategy = ReplicatorStrategy(raft)) {
  private val broadcastInterval: Long

  init {
    LOG.info("Replication target $target")
    broadcastInterval = raft.config.getAsLong(Config.KEY_RAFT_LEADER_BROADCAST_INTERVAL_MS)
  }

  suspend fun start() {
    /* send heartbeat to followers as newly elected leader, we want
      to ensure we account for overhead of initializing the new leader state */
    sendHeartbeat()
    while (true) {
      /* Delay for remainder of broadcast interval and the time taken to replicate to target. If exceeded,
        zero delay, in which case a follower has probably timed out and transitioned to a candidate state. */
      delay(max(broadcastInterval - measureTimeMillis { replicate() }, 0))
    }
  }

  private suspend fun replicate() {
    /* determine next entries to replicate */
    val nextIndex = leaderContext.getNextIndex(target)
    raft.clusterMessenger.dispatch<AppendEntriesResponse>(
        target,
        replicatorStrategy.generateRequest(nextIndex),
        { response -> handleResponse(response, nextIndex) })
  }

  private fun handleResponse(response: AppendEntriesResponse, nextIndex: Long) {
    if (response.isSuccessful) {
      with(leaderContext) {
        updateMatchIndex(target, raft.log.lastIndex)
        updateNextIndex(target, raft.log.lastIndex + 1)
      }
    } else {
      LOG.info("Rolling back nextIndex")
      leaderContext.updateNextIndex(target, max(nextIndex - 1, 0))
    }
  }

  private suspend fun sendHeartbeat() {
    with(raft) {
      clusterMessenger.dispatch<AppendEntriesResponse>(target, AppendEntriesRequest.builder(
          raftContext.currentTerm,
          log.lastEntry.term,
          log.lastIndex,
          clusterSet.localNode.id,
          log.commitIndex).build())
    }
  }

  companion object {
    private val LOG = Logger.getLogger(ReplicationController::class.java)
  }
}
