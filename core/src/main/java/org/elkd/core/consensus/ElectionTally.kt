package org.elkd.core.consensus

import org.apache.log4j.Logger
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.server.cluster.Node

/**
 * ElectionMode is used to determine when an election is over and what determines
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
enum class ElectionMode {
  MAJORITY
}

/**
 * Keeps record of nodes who have voted in a term.
 *
 * @param voteRequest
 * @param electionMode Determines when election is over. {@link ElectionMode}
 * @param voteTotal Total expected votes (usually number of nodes in cluster.
 * @param onSuccess Hook to call when election was successful
 * @param onFailure Hook to call when election was unsuccessful
 */
class ElectionTally(private val voteRequest: RequestVoteRequest,
                    private val voteTotal: Int,
                    private val electionMode: ElectionMode,
                    private val onSuccess: Runnable?,
                    private val onFailure: Runnable?) {
  private val voted: MutableSet<Node> = mutableSetOf()
  private var upVotes = 0
  private var downVotes = 0

  @Synchronized fun recordVote(node: Node) {
    if (!(node in voted)) {
      upVotes++
      voted.add(node)
    }
    postVoteChecks()
  }

  @Synchronized fun recordNoVote(node: Node) {
    if (!(node in voted)) {
      downVotes++
      voted.add(node)
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
    return when (electionMode) {
      ElectionMode.MAJORITY -> isMajorityElectionOver()
    }
  }

  private fun wasElectionSuccessful(): Boolean {
    return when(electionMode) {
      ElectionMode.MAJORITY -> wasMajorityElectionSuccessful()
    }
  }

  private fun isMajorityElectionOver(): Boolean {
    return voted.size > (voteTotal / 2);
  }

  private fun wasMajorityElectionSuccessful(): Boolean {
    return upVotes > (voteTotal / 2);
  }

  override fun toString(): String {
    return "ElectionTally(voteTotal=$voteTotal, electionMode=$electionMode, voted=$voted, upVotes=$upVotes, downVotes=$downVotes)"
  }

  companion object {
    private val LOG = Logger.getLogger(ElectionTally::class.java.name)
  }
}
