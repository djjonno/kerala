package org.elkd.core.consensus.replication

import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.Log
import kotlin.math.max


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
    return when(hasNewEntries(nextIndex)) {
      true -> generateRequest(log.read(nextIndex, log.lastIndex))
      false -> generateRequest(emptyList())
    }
  }

  /*
   * Build AppendEntriesRequest with given entry list.
   */
  private fun generateRequest(entries: List<Entry>): AppendEntriesRequest {
    val prevLogIndex = max(0, log.lastIndex - entries.size)
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
}
