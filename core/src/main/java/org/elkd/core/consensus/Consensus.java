package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import org.elkd.core.cluster.ClusterConfig;

import javax.annotation.Nonnull;

public class Consensus {
  private Delegate mDelegate;
  private ClusterConfig mClusterConfig;
  private NodeState mNodeState;
  private AbstractDelegateFactory mStateFactory;

  public Consensus(@Nonnull final ClusterConfig clusterConfig,
                   @Nonnull final NodeState nodeState,
                   @Nonnull final AbstractDelegateFactory delegateFactory) {
    mClusterConfig = Preconditions.checkNotNull(clusterConfig, "clusterConfig");
    mNodeState = Preconditions.checkNotNull(nodeState, "nodeState");
    mStateFactory = Preconditions.checkNotNull(delegateFactory, "delegateFactory");
  }

  public void initialize() {
    mDelegate = mStateFactory.getInitialDelegate(this);
    mDelegate.on();
  }

  public AppendEntriesResponse delegateAppendEntries(final AppendEntriesRequest request) {
    return mDelegate.delegateAppendEntries(request);
  }

  public RequestVotesResponse delegateRequestVotes(final RequestVotesRequest request) {
    return mDelegate.delegateRequestVotes(request);
  }

  void transition(@Nonnull final Class<? extends Delegate> newDelegate) {
    mDelegate.off();
    mDelegate = mStateFactory.getDelegate(this, newDelegate);
    mDelegate.on();
  }

  /* package-private */ NodeState getContext() {
    return mNodeState;
  }

  /* package-private */ ClusterConfig getClusterConfig() {
    return mClusterConfig;
  }
}
