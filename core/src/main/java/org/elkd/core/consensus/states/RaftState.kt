package org.elkd.core.consensus.states

import org.elkd.core.consensus.RaftDelegate
import org.elkd.core.consensus.TransitiveState

enum class State {
  FOLLOWER,
  CANDIDATE,
  LEADER
}

interface RaftState : RaftDelegate, TransitiveState
