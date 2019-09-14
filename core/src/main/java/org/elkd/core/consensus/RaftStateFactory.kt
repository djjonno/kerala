package org.elkd.core.consensus

class RaftStateFactory(private val raft: Raft) {
  fun getState(state: State): RaftState {
    return when(state) {
      State.FOLLOWER -> RaftFollowerState(raft)
      State.CANDIDATE -> RaftCandidateState(raft)
      State.LEADER -> RaftLeaderState(raft)
    }
  }
}
