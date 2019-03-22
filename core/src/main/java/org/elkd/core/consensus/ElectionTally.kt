package org.elkd.core.consensus

import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.server.cluster.Node

/**
 * Keeps record of nodes who have voted in a term.
 *
 * @param voteRequest
 * @param electionType Determines when election is over. {@link ElectionType}
 * @param voteTotal Total expected votes (usually number of nodes in cluster.
 */
class ElectionTally(private val voteRequest: RequestVoteRequest,
                    private val voteTotal: Int,
                    private val electionType: ElectionType,
                    private val onSuccess: Runnable?,
                    private val onFailure: Runnable?) {
  private val voted: Set<Node> = mutableSetOf()
  private var upVotes = 0
  private var downVotes = 0

  fun recordVote(node: Node) {
    if (!(node in voted)) {
      upVotes++
      voted.plus(node)
    }
    postVoteChecks()
  }

  fun recordNoVote(node: Node) {
    if (!(node in voted)) {
      downVotes++
      voted.plus(node)
    }
    postVoteChecks()
  }

  private fun postVoteChecks() {
    if (isElectionOver()) {
      when (wasElectionSuccessful()) {
        true -> onSuccess?.run()
        false -> onFailure?.run()
      }
    }
  }

  private fun isElectionOver(): Boolean {
    return when (electionType) {
      ElectionType.MAJORITY -> isMajorityElectionOver()
    }
  }

  private fun wasElectionSuccessful(): Boolean {
    return when(electionType) {
      ElectionType.MAJORITY -> wasMajorityElectionSuccessful()
    }
  }

  private fun isMajorityElectionOver(): Boolean {
    return voted.size >= (voteTotal / 2);
  }

  private fun wasMajorityElectionSuccessful(): Boolean {
    return upVotes >= (voteTotal / 2);
  }
}

/**
 * ElectionType is used to determine when an election is over and what determines
 * a successful election round.
 *
 * MAJORITY: votes required `cluster.size / 2`. Election is over when:
 *    - majority is met OR
 *    - pending votes will not make a majority.
 *    (which ever comes first)
 *
 * Election is finished when the votes required is either reached or all expected
 * votes are in but have not achieved.
 */
enum class ElectionType {
  MAJORITY
}
