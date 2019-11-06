package org.kerala.core.consensus.states

import org.kerala.core.consensus.RaftDelegate
import org.kerala.core.consensus.TransitiveState

enum class State {
  FOLLOWER,
  CANDIDATE,
  LEADER
}

interface RaftState : RaftDelegate, TransitiveState
