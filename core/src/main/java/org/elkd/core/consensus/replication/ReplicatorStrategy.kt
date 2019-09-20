package org.elkd.core.consensus.replication

import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.Log
import kotlin.math.max
import kotlin.math.min


/**
 * ReplicatorStrategy will provide the necessary AppendEntriesRequest object
 * given the target node and it's current state in the LeaderContext.
 *
 * The component is not a singleton purely for testing purposes (we want
 * to preserve the ability to mock the object and it's behavior).
 */
class ReplicatorStrategy(private val raft: Raft) {
  private val log: Log<Entry> = raft.log

  fun generateRequest(nextIndex: Long): AppendEntriesRequest {
    return if (hasNewEntries(nextIndex)) {
      generateRequest(nextIndex, log.read(nextIndex, min(nextIndex + MAX_APPEND_ENTRIES - 1, log.lastIndex)))
    } else {
      generateRequest(nextIndex, emptyList())
    }
  }

  /*
   * Build AppendEntriesRequest with given entry list.
   */
  private fun generateRequest(nextIndex: Long, entries: List<Entry>): AppendEntriesRequest {
    val prevLogIndex = max(0, nextIndex - 1)
    val prevLogTerm = log.read(prevLogIndex)?.term!!
    return AppendEntriesRequest.builder(
        raft.raftContext.currentTerm,
        prevLogTerm,
        prevLogIndex,
        raft.clusterSet.localNode.id,
        log.commitIndex
    )
        .withEntries(entries)
        .build()
  }

  private fun hasNewEntries(nextIndex: Long): Boolean {
    return log.lastIndex >= nextIndex
  }

  companion object {
    // Represents the max number of entries will be included in a AppendEntriesRequest
    private const val MAX_APPEND_ENTRIES = 10000
  }
}
