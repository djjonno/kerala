package org.ravine.core.consensus.replication

import kotlin.math.min
import org.ravine.core.consensus.Raft
import org.ravine.core.consensus.messages.AppendEntriesRequest
import org.ravine.core.consensus.messages.Entry
import org.ravine.core.log.ds.Log
import org.ravine.core.log.ds.LogSnapshot
import org.ravine.core.runtime.topic.Topic

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
    return if (entriesReady(nextIndex)) {
      topic.logFacade.readBlock {
        createRequest(log.readSnapshot(nextIndex, min(nextIndex + MAX_APPEND_ENTRIES - 1, log.lastIndex)))
      }
    } else {
      createEmptyRequest()
    }
  }

  /*
   * Build AppendEntriesRequest with given entry list.
   */
  private fun createRequest(snapshot: LogSnapshot<Entry>): AppendEntriesRequest {
    return AppendEntriesRequest(
        term = raft.raftContext.currentTerm,
        topicId = topic.id,
        prevLogTerm = snapshot.prevLogTerm,
        prevLogIndex = snapshot.prevLogIndex,
        leaderId = raft.clusterSet.selfNode.id,
        leaderCommit = snapshot.commitIndex,
        entries = snapshot.entries
    )
  }

  private fun createEmptyRequest(): AppendEntriesRequest {
    return AppendEntriesRequest(
        term = raft.raftContext.currentTerm,
        topicId = topic.id,
        prevLogTerm = log.lastEntry.term,
        prevLogIndex = log.lastIndex,
        leaderId = raft.clusterSet.selfNode.id,
        leaderCommit = log.commitIndex,
        entries = emptyList()
    )
  }

  private fun entriesReady(nextIndex: Long): Boolean {
    return log.lastIndex > 0 && log.lastIndex >= nextIndex
  }

  private companion object {
    // Represents the max number of entries will be included in a AppendEntriesRequest
    const val MAX_APPEND_ENTRIES = 100
  }
}
