package org.ravine.core.consensus.states

import org.ravine.core.consensus.Raft
import org.ravine.core.consensus.states.State.CANDIDATE
import org.ravine.core.consensus.states.State.FOLLOWER
import org.ravine.core.consensus.states.State.LEADER
import org.ravine.core.consensus.states.candidate.RaftCandidateState
import org.ravine.core.consensus.states.follower.RaftFollowerState
import org.ravine.core.consensus.states.leader.RaftLeaderState

class RaftStateFactory(private val raft: Raft) {
  fun getState(state: State) = when (state) {
    FOLLOWER -> RaftFollowerState(raft)
    CANDIDATE -> RaftCandidateState(raft)
    LEADER -> RaftLeaderState(raft)
  }
}
