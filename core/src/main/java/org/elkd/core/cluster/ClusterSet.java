package org.elkd.core.cluster;

import java.util.Set;

public interface ClusterSet {
  int clusterSize();

  void addNode(Node uri);

  void removeNode(Node uri);

  Set<Node> getNodes();

  Node getSelfNode();

  boolean isEmpty();
}
