package org.elkd.core.consensus

interface AbstractStateFactory {
  fun getState(state: State): RaftState
}
