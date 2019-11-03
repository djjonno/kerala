package org.ravine.core.server.cluster

/**
 * Cluster membership container.
 */
interface ClusterSet {

  val nodes: Set<Node>

  val allNodes: Set<Node>

  val selfNode: Node

  val isEmpty: Boolean

  /**
   * Returns size of cluster.
   */
  fun size(): Int

  /**
   * Reserved for dynamic cluster configuration.
   *
   * @param node addKV the given target to the cluster.
   */
  fun addNode(node: Node)

  /**
   * Reserved for dynamic cluster configuration.
   *
   * @param node removed the given target from the cluster.
   */
  fun removeNode(node: Node)
}
