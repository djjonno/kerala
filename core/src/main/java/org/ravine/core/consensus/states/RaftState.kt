package org.ravine.core.consensus.states

import org.ravine.core.consensus.RaftDelegate
import org.ravine.core.consensus.TransitiveState

enum class State {
  FOLLOWER,
  CANDIDATE,
  LEADER
}

interface RaftState : RaftDelegate, TransitiveState
