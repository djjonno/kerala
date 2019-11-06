package org.kerala.core.consensus.states

import org.kerala.core.consensus.Raft
import org.kerala.core.consensus.states.State.CANDIDATE
import org.kerala.core.consensus.states.State.FOLLOWER
import org.kerala.core.consensus.states.State.LEADER
import org.kerala.core.consensus.states.candidate.RaftCandidateState
import org.kerala.core.consensus.states.follower.RaftFollowerState
import org.kerala.core.consensus.states.leader.RaftLeaderState

class RaftStateFactory(private val raft: Raft) {
  fun getState(state: State) = when (state) {
    FOLLOWER -> RaftFollowerState(raft)
    CANDIDATE -> RaftCandidateState(raft)
    LEADER -> RaftLeaderState(raft)
  }
}
