package org.elkd.core.server.cluster;

import java.util.Set;

/**
 * Cluster membership container.
 */
public interface ClusterSet {
  int size();

  /**
   * Reserved for dynamic cluster configuration.
   *
   * @param node addKV the given target to the cluster.
   */
  void addNode(Node node);

  /**
   * Reserved for dynamic cluster configuration.
   *
   * @param node removed the given target from the cluster.
   */
  void removeNode(Node node);

  Set<Node> getNodes();

  Set<Node> getAllNodes();

  Node getLocalNode();

  boolean isEmpty();
}
