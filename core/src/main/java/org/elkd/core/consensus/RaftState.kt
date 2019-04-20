package org.elkd.core.consensus

enum class State {
  FOLLOWER,
  CANDIDATE,
  LEADER
}

/* This is a composite for  */
interface RaftState : RaftDelegate, TransitiveState
