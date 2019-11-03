package org.ravine.core.consensus.states.candidate.election

/**
 * MajorityElectionStrategy requires a majority vote to be considered
 * a successful election.
 *
 * let, upVotes = total number of 'yes' votes received.
 * let, downVotes = total number of 'no' votes received.
 * let, totalVotes = max number of votes in election round.
 *
 * Majority vote
 * successful: upVotes > totalVotes / 2
 * unsuccessful: upVotes + downVotes > totalVotes / 2
 *
 * A majority election round is over when votes received (upVotes + downVotes)
 * exceeds the majority vote threshold (totalVotes / 2)
 */
class MajorityElectionStrategy : ElectionStrategy {
  override fun isComplete(electionTally: ElectionTally): Boolean {
    return electionTally.totalVotes() > (electionTally.expectedVotes / 2)
  }

  override fun isSuccessful(electionTally: ElectionTally): Boolean {
    return electionTally.totalUpVotes() > (electionTally.expectedVotes / 2)
  }
}
