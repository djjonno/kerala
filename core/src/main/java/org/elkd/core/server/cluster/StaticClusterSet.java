package org.elkd.core.server.cluster;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.elkd.core.ElkdRuntimeException;
import org.elkd.shared.schemes.URI;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class StaticClusterSet implements ClusterSet {
  private final ImmutableSet<Node> mNodes;
  private final Node mSelfNode;

  private StaticClusterSet(final Set<Node> nodes, final Node selfNode) {
    mNodes = ImmutableSet.copyOf(nodes);
    mSelfNode = selfNode;
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
  public Set<Node> getAllNodes() {
    return ImmutableSet.<Node>builder()
        .addAll(mNodes)
        .add(mSelfNode)
        .build();
  }

  @Override
  public Node getLocalNode() {
    return mSelfNode;
  }

  @Override
  public int size() {
    return mNodes.size();
  }

  @Override
  public boolean isEmpty() {
    return mNodes.isEmpty();
  }

  public static Builder builder(final Node selfNode) {
    return new Builder(selfNode);
  }

  public static class Builder {
    private Set<Node> mNodes = new HashSet<>();
    private Node mSelfNode;

    public Builder(final Node selfNode) {
      this.mSelfNode = Preconditions.checkNotNull(selfNode, "selfNode");
    }

    public Builder withString(final String clusterSet) {
      Preconditions.checkNotNull(clusterSet, "clusterSet");
      Stream.of(clusterSet.split(","))
          .filter(uri -> !uri.isEmpty())
          .map(URI::parseURIString)
          .forEach(uri -> {
            withNode(new Node(uri));
          });

      return this;
    }

    public Builder withNode(final Node node) {
      Preconditions.checkNotNull(node, "target");
      if (node.equals(mSelfNode)) {
        return this;
      }
      if (mNodes.contains(node)) {
        throw new ElkdRuntimeException(node + " duplicate target.");
      }
      mNodes.add(node);
      return this;
    }

    public StaticClusterSet build() {
      return new StaticClusterSet(mNodes, mSelfNode);
    }
  }

  @Override
  public String toString() {
    return "StaticClusterSet[" +
        "self=" + mSelfNode +
        ", nodes=" + mNodes +
        ']';
  }
}
