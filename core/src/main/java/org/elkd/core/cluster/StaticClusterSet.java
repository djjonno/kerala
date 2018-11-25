package org.elkd.core.cluster;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.apache.log4j.Logger;
import org.elkd.core.ElkdRuntimeException;
import org.elkd.shared.schemes.URI;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class StaticClusterSet implements ClusterSet {
  private static final Logger LOG = Logger.getLogger(StaticClusterSet.class);

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
  public Node getSelfNode() {
    return mSelfNode;
  }

  @Override
  public int clusterSize() {
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
      Preconditions.checkNotNull(node, "node");
      if (node.equals(mSelfNode)) {
        return this;
      }
      if (mNodes.contains(node)) {
        throw new ElkdRuntimeException(node + " duplicate node.");
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
    return "StaticClusterSet{" +
        "mNodes=" + mNodes +
        ", mSelfNode=" + mSelfNode +
        '}';
  }
}
