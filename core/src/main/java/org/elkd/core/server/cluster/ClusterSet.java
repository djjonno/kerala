package org.elkd.core.server.cluster;

import java.util.Set;

public interface ClusterSet {
  int clusterSize();

  void addNode(Node uri);

  void removeNode(Node uri);

  Set<Node> getNodes();

  Set<Node> getAllNodes();

  Node getSelfNode();

  boolean isEmpty();
}
