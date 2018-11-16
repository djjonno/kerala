package org.elkd.core.cluster;

import com.google.common.collect.ImmutableSet;
import org.elkd.core.ElkdRuntimeException;

import java.util.HashSet;
import java.util.Set;

public final class StaticClusterSet implements ClusterSet {
  private final ImmutableSet<Node> mNodes;

  private StaticClusterSet(final Set<Node> nodes) {
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Set<Node> mNodes = new HashSet<>();

    public Builder withNode(final Node node) {
      if (mNodes.contains(node)) {
        throw new ElkdRuntimeException(node + " is already a member. Check the node-id");
      }
      mNodes.add(node);
      return this;
    }

    public StaticClusterSet build() {
      return new StaticClusterSet(mNodes);
    }
  }
}
