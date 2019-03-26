package org.elkd.core.consensus.election

import org.elkd.shared.annotations.Mockable

/**
 * Keeps record of nodes who have voted in a term.
 *
 * @param expectedVotes Total expected votes (usually number of nodes in cluster.
 */
@Mockable
class ElectionTally(val expectedVotes: Int) {
  private val upVotes: Set<String> = mutableSetOf()
  private val downVotes: Set<String> = mutableSetOf()

  fun totalUpVotes() = upVotes.size
  fun totalDownVotes() = downVotes.size

  fun recordUpVote(id: String) {
    (upVotes as MutableSet).add(id)
  }

  fun recordDownVote(id: String) {
    (downVotes as MutableSet).add(id)
  }

  fun totalVotes(): Int {
    return upVotes.size + downVotes.size
  }

  override fun toString(): String {
    return "ElectionTally(expectedVotes=$expectedVotes, voted=${upVotes.size + downVotes.size}, upVotes=$upVotes, downVotes=$downVotes)"
  }
}
