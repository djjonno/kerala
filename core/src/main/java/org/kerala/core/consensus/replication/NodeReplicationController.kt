package org.kerala.core.consensus.replication

import kotlin.math.max
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.delay
import org.apache.log4j.Logger
import org.kerala.core.consensus.Raft
import org.kerala.core.consensus.messages.AppendEntriesRequest
import org.kerala.core.consensus.messages.AppendEntriesResponse
import org.kerala.core.consensus.states.leader.LeaderContext
import org.kerala.core.runtime.topic.Topic
import org.kerala.core.server.cluster.Node
import org.kerala.shared.annotations.Mockable

/**
 * NodeReplicationController replicates the given raft state to the target {$link Node}.
 *
 * @see Node
 * @see Replicator
 */
@Mockable
class NodeReplicationController(
    val raft: Raft,
    val topic: Topic,
    val target: Node,
    val leaderContext: LeaderContext,
    private val broadcastInterval: Long,
    private val replicatorStrategy: ReplicatorStrategy = ReplicatorStrategy(topic, raft)
) {
  init {
    LOGGER.info("replicating $topic -> $target")
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
    val request = replicatorStrategy.generateRequest(nextIndex)
    raft.clusterMessenger.dispatchAppendEntries(target, request, onSuccess = { response ->
      handleResponse(request, response, nextIndex)
    })
  }

  private fun handleResponse(request: AppendEntriesRequest, response: AppendEntriesResponse, nextIndex: Long) {
    if (response.isSuccessful) {
      with(leaderContext) {
        updateMatchIndex(target, request.prevLogIndex + request.entries.size)
        updateNextIndex(target, request.prevLogIndex + request.entries.size + 1)
      }
    } else {
      leaderContext.updateNextIndex(target, response.prevLogIndex + 1)
    }
  }

  private suspend fun sendHeartbeat() {
    with(topic.logFacade) {
      val message = AppendEntriesRequest(
          term = raft.raftContext.currentTerm,
          topicId = topic.id,
          prevLogTerm = log.lastEntry.term,
          prevLogIndex = log.lastIndex,
          leaderId = raft.clusterSet.selfNode.id,
          leaderCommit = log.commitIndex)
      raft.clusterMessenger.dispatchAppendEntries(target, message)
    }
  }

  companion object {
    private val LOGGER = Logger.getLogger(NodeReplicationController::class.java)
  }
}
