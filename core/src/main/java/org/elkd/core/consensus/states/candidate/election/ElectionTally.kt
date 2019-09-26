package org.elkd.core.consensus.states.candidate.election

import org.apache.log4j.Logger
import org.elkd.shared.annotations.Mockable

/**
 * Keeps record of nodes who have voted in a term.
 *
 * @param expectedVotes Total expected votes (usually number of nodes in cluster.
 */
@Mockable
class ElectionTally(val expectedVotes: Int) {
  private val votes: Set<String> = mutableSetOf()
  private var upVotes = 0
  private var downVotes = 0

  fun totalVotes() = votes.size
  fun totalUpVotes() = upVotes
  fun totalDownVotes() = downVotes

  fun recordUpVote(id: String) {
    if (checkAndIncrement(id)) {
      LOGGER.info("up vote recorded; voter $id")
      upVotes++
    }
  }

  fun recordDownVote(id: String) {
    if (checkAndIncrement(id)) {
      LOGGER.info("down vote recorded; voter $id")
      downVotes++
    }
  }

  fun checkAndIncrement(id: String): Boolean {
    val isNew = !(id in votes)
    (votes as MutableSet).add(id)
    return isNew
  }

  override fun toString(): String {
    return "ElectionTally(expectedVotes=$expectedVotes, votes=$votes, upVotes=$upVotes, downVotes=$downVotes)"
  }

  private companion object {
    var LOGGER = Logger.getLogger(ElectionTally::class.java)
  }
}
