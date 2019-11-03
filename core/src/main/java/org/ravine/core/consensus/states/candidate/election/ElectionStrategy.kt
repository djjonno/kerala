package org.ravine.core.consensus.states.candidate.election

interface ElectionStrategy {
  /**
   * @return true if the election is over; enough votes received to make a decision.
   */
  fun isComplete(electionTally: ElectionTally): Boolean

  /**
   * @return true if the election was successful.
   */
  fun isSuccessful(electionTally: ElectionTally): Boolean
}
