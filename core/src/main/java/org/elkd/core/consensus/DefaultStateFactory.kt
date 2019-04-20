package org.elkd.core.consensus

class DefaultStateFactory(private val raft: Raft) : AbstractStateFactory {
  override fun getState(state: State): RaftState {
    return when(state) {
      State.FOLLOWER -> RaftFollowerState(raft)
      State.CANDIDATE -> RaftCandidateState(raft)
      State.LEADER -> RaftLeaderState(raft)
    }
  }
}
