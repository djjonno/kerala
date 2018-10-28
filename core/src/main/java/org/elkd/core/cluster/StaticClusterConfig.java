package org.elkd.core.cluster;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class StaticClusterConfig implements ClusterConfig {
  private final ImmutableSet<Node> mNodes;

  public StaticClusterConfig(final Set<Node> nodes) {
    mNodes = ImmutableSet.copyOf(nodes);
  }

  public void addNode(final Node node) {
    // no-op, membership is static
  }

  @Override
  public void removeNode(final Node node) {
    // no-op, membership is static
  }

  @Override
  public Set<Node> getNodes() {
    return mNodes;
  }

  @Override
  public int clusterSize() {
    return mNodes.size();
  }
}
