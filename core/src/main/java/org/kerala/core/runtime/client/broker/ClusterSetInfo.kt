package org.kerala.core.runtime.client.broker

import org.kerala.core.server.cluster.ClusterSet
import org.kerala.core.server.cluster.Node

class ClusterSetInfo(val clusterSet: ClusterSet) {
  var leader: Node? = null

  fun describe(): Set<Node> {
    return clusterSet.allNodes
  }
}
