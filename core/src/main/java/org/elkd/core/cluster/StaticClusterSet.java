package org.elkd.core.cluster;

import com.google.common.collect.ImmutableSet;
import org.elkd.core.ElkdRuntimeException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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

  @Override
  public boolean isEmpty() {
    return mNodes.isEmpty();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Set<Node> mNodes = new HashSet<>();

    public Builder withString(final String clusterSet) {
      Stream.of(clusterSet.split(","))
          .filter(s -> !s.isEmpty())
          .forEach(s -> {
            mNodes.add(new Node(s));
          });

      return this;
    }

    public Builder withNode(final Node node) {
      if (mNodes.contains(node)) {
        throw new ElkdRuntimeException(node + " duplicate node.");
      }
      mNodes.add(node);
      return this;
    }

    public StaticClusterSet build() {
      return new StaticClusterSet(mNodes);
    }
  }

  @Override
  public String toString() {
    return "StaticClusterSet{" +  mNodes + '}';
  }
}
