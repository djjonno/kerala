package org.kerala.core.consensus.states.candidate.election

import org.kerala.shared.annotations.Mockable

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

  val totalVotes
    get() = votes.size
  val totalUpVotes
    get() = upVotes

  fun recordUpVote(id: String) {
    if (checkAndIncrement(id)) {
      upVotes++
    }
  }

  fun recordDownVote(id: String) {
    if (checkAndIncrement(id)) {
      downVotes++
    }
  }

  @Synchronized private fun checkAndIncrement(id: String): Boolean {
    val isNew = !(id in votes)
    (votes as MutableSet).add(id)
    return isNew
  }

  override fun toString(): String {
    return "ElectionTally(expectedVotes=$expectedVotes, votes=$votes, upVotes=$upVotes, downVotes=$downVotes)"
  }
}
