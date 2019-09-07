package org.elkd.core.consensus

enum class State {
  FOLLOWER,
  CANDIDATE,
  LEADER
}

interface RaftState : RaftDelegate, TransitiveState
