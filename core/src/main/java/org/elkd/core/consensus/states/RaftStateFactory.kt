package org.elkd.core.consensus.states

import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.states.State.CANDIDATE
import org.elkd.core.consensus.states.State.FOLLOWER
import org.elkd.core.consensus.states.State.LEADER
import org.elkd.core.consensus.states.candidate.RaftCandidateState
import org.elkd.core.consensus.states.follower.RaftFollowerState
import org.elkd.core.consensus.states.leader.RaftLeaderState

class RaftStateFactory(private val raft: Raft) {
  fun getState(state: State) = when(state) {
    FOLLOWER -> RaftFollowerState(raft)
    CANDIDATE -> RaftCandidateState(raft)
    LEADER -> RaftLeaderState(raft)
  }
}
