package org.kerala.core.server.cluster

import java.util.HashSet
import org.kerala.core.KeralaRuntimeException
import org.kerala.shared.schemes.URI

class StaticClusterSet private constructor(
    override val nodes: Set<Node>,
    override val selfNode: Node
) : ClusterSet {
  override val allNodes: Set<Node> = setOf(elements = *nodes.toTypedArray() + selfNode)

  override val isEmpty: Boolean
    get() = nodes.isEmpty()

  override fun addNode(node: Node) {
    // no-op, membership is static
  }

  override fun removeNode(node: Node) {
    // no-op, membership is static
  }

  override fun size(): Int {
    return nodes.size
  }

  override fun toString(): String {
    return "StaticClusterSet(nodes=$nodes, self=$selfNode)"
  }

  class Builder(private val selfNode: Node) {
    private val nodes = HashSet<Node>()

    fun withString(clusterSet: String): Builder {
      clusterSet.split(",".toRegex())
          .filter { it.isNotEmpty() }
          .map(URI::parseURIString)
          .forEach {
            withNode(Node(it))
          }
      return this
    }

    fun withNode(node: Node): Builder {
      if (node == selfNode) {
        return this
      }
      if (nodes.contains(node)) {
        throw KeralaRuntimeException("$node duplicate target.")
      }
      nodes.add(node)
      return this
    }

    fun build(): StaticClusterSet {
      return StaticClusterSet(nodes, selfNode)
    }
  }

  companion object {
    fun builder(selfNode: Node): Builder {
      return Builder(selfNode)
    }
  }
}
