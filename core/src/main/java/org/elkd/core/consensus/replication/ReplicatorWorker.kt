package org.elkd.core.consensus.replication

import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.consensus.LeaderContext
import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.server.cluster.Node
import kotlin.coroutines.CoroutineContext
import kotlin.math.max

/**
 * ReplicationWorker replicates the given raft state to the target {$link Node}.
 *
 * @see Node
 * @see Replicator
 */
class ReplicatorWorker(val target: Node,
                       val leaderContext: LeaderContext,
                       val raft: Raft,
                       val coroutineContext: CoroutineContext) {
  private val broadcastInterval: Long

  init {
    LOG.info("replication target: $target (in $coroutineContext)")
    broadcastInterval = raft.config.getAsLong(Config.KEY_RAFT_LEADER_BROADCAST_INTERVAL_MS)
  }

  suspend fun start() {
    sendHeartbeat()

    while (true) {
      // determine next entries to replicate
      val nextIndex = leaderContext.getNextIndex(target)
      LOG.info("${raft.log.lastIndex}, $nextIndex")

      val nextEntries = mutableListOf<Entry>()
      var prevLogIndex = raft.log.lastIndex

      if (raft.log.lastIndex >= nextIndex) {
        nextEntries += raft.log.read(nextIndex, raft.log.lastIndex)
        prevLogIndex -= nextEntries.size
        LOG.info("replicating $nextEntries to ${target.id}")
      }

      val prevLogTerm = raft.log.read(prevLogIndex)?.term

      val listenableFuture = raft.clusterMessenger.appendEntries(target, AppendEntriesRequest
          .builder(raft.raftContext.currentTerm, prevLogTerm!!, prevLogIndex, raft.clusterSet.localNode().id, -1)
          .withEntries(nextEntries)
          .build())

      listenableFuture.addListener(Runnable {
        try {
          if (listenableFuture.get().isSuccessful) {
            leaderContext.updateMatchIndex(target, raft.log.lastIndex)
            leaderContext.updateNextIndex(target, raft.log.lastIndex + 1)
          } else {
            LOG.info("rolling back nextIndex")
            leaderContext.updateNextIndex(target, max(nextIndex - 1, 0))
          }
        } catch (e: Exception) {
          LOG.info("unreachable: $target")
        }
      }, MoreExecutors.directExecutor())
      delay(400)
    }
  }

  private fun sendHeartbeat() {
    raft.clusterMessenger.appendEntries(target, AppendEntriesRequest.builder(raft.raftContext.currentTerm, raft.log.lastEntry.term, raft.log.lastIndex, raft.clusterSet.localNode().id, -1).build())
  }

  companion object {
    private val LOG = Logger.getLogger(ReplicatorWorker::class.java)
  }
}
