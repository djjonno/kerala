package org.elkd.core.consensus.replication

import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.ds.Log
import org.elkd.core.runtime.topic.Topic
import kotlin.math.max
import kotlin.math.min


/**
 * ReplicatorStrategy will provide the necessary AppendEntriesRequest object
 * given the target node and it's current state in the LeaderContext.
 *
 * The component is not a singleton purely for testing purposes (we want
 * to preserve the ability to mock the object and it's behavior).
 */
class ReplicatorStrategy(val topic: Topic, val raft: Raft) {
  private val log: Log<Entry> = topic.logFacade.log

  fun generateRequest(nextIndex: Long): AppendEntriesRequest {
    return generateRequest(nextIndex, if (entriesReady(nextIndex)) {
      log.read(nextIndex, min(nextIndex + MAX_APPEND_ENTRIES - 1, log.lastIndex))
    } else {
      emptyList()
    })
  }

  /*
   * Build AppendEntriesRequest with given entry list.
   */
  private fun generateRequest(nextIndex: Long, entries: List<Entry>): AppendEntriesRequest {
    val prevLogIndex = max(0, nextIndex - 1)
    val prevLogTerm = log.read(prevLogIndex)?.term!!
    return AppendEntriesRequest(
        term = raft.raftContext.currentTerm,
        topicId = topic.id,
        prevLogTerm = prevLogTerm,
        prevLogIndex = prevLogIndex,
        leaderId = raft.clusterSet.localNode.id,
        leaderCommit = log.commitIndex,
        entries = entries
    )
  }

  private fun entriesReady(nextIndex: Long): Boolean {
    return log.lastIndex >= nextIndex
  }

  companion object {
    // Represents the max number of entries will be included in a AppendEntriesRequest
    private const val MAX_APPEND_ENTRIES = 10000
  }
}
