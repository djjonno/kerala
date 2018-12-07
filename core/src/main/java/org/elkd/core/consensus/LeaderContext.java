package org.elkd.core.consensus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.elkd.core.log.Log;
import org.elkd.core.server.cluster.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LeaderContext {
  @VisibleForTesting static final long DEFAULT_MATCH_INDEX = Log.INDEX_FROM;

  private final Map<Node, Long> mNextIndex = new HashMap<>();
  private final Map<Node, Long> mMatchIndex = new HashMap<>();

  public LeaderContext(final Set<Node> nodes, final RaftContext context) {
    Preconditions.checkNotNull(nodes, "nodes");

    for (final Node node : nodes) {
      mNextIndex.put(node, context.getLogInvoker().getLastIndex() + 1);
      mMatchIndex.put(node, Log.INDEX_FROM);
    }
  }

  long getNextIndex(final Node node) {
    Preconditions.checkNotNull(node, "node");
    Preconditions.checkState(mNextIndex.containsKey(node), "node not found in context");

    return mNextIndex.get(node);
  }

  long getMatchIndex(final Node node) {
    Preconditions.checkNotNull(node, "node");
    Preconditions.checkState(mMatchIndex.containsKey(node), "node not found in context");

    return mMatchIndex.get(node);
  }

  void updateNextIndex(final Node node, final long index) {
    Preconditions.checkNotNull(node, "node");
    Preconditions.checkState(mNextIndex.containsKey(node), "node not found in context");

    mNextIndex.put(node, index);
  }

  void updateMatchIndex(final Node node, final long index) {
    Preconditions.checkNotNull(node, "node");
    Preconditions.checkState(mMatchIndex.containsKey(node));

    mMatchIndex.put(node, index);
  }
}
