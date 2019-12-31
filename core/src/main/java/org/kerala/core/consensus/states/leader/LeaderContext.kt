package org.kerala.core.consensus.states.leader

import com.google.common.base.Preconditions
import org.kerala.core.server.cluster.Node
import java.util.HashMap
import kotlin.math.max

class LeaderContext(nodes: Set<Node>, lastIndex: Long) {
  private val nextIndex = HashMap<Node, Long>()
  private val matchIndex = HashMap<Node, Long>()

  init {
    nodes.forEach { node ->
      nextIndex[node] = lastIndex + 1
      matchIndex[node] = DEFAULT_MATCH_INDEX
    }
  }

  fun getNextIndex(node: Node): Long {
    Preconditions.checkNotNull(node, "node")
    Preconditions.checkState(node in nextIndex, "node not found in context")

    return nextIndex[node]!! /* node is guaranteed to be in this set */
  }

  fun getMatchIndex(node: Node): Long {
    Preconditions.checkNotNull(node, "node")
    Preconditions.checkState(node in matchIndex, "node not found in context")

    return matchIndex[node]!! /* node is guaranteed to be in this set */
  }

  fun updateNextIndex(node: Node, index: Long) {
    Preconditions.checkNotNull(node, "node")
    Preconditions.checkState(node in nextIndex, "node not found in context")

    nextIndex[node] = max(MIN_NEXT_INDEX, index)
  }

  fun updateMatchIndex(node: Node, index: Long) {
    Preconditions.checkNotNull(node, "node")
    Preconditions.checkState(node in matchIndex)

    matchIndex[node] = index
  }

  override fun toString(): String {
    return "LeaderContext(nextIndex=$nextIndex, matchIndex=$matchIndex)"
  }

  companion object {
    const val MIN_NEXT_INDEX = 0L
    const val DEFAULT_MATCH_INDEX = -1L
  }
}
