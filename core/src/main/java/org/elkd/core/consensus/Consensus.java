package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import org.elkd.core.cluster.ClusterConfig;

import javax.annotation.Nonnull;

public class Consensus {
  private Delegate mDelegate;
  private ClusterConfig mClusterConfig;
  private ConsensusContext mConsensusContext;
  private AbstractDelegateFactory mStateFactory;

  public Consensus(@Nonnull final ClusterConfig clusterConfig,
                   @Nonnull final ConsensusContext consensusContext,
                   @Nonnull final AbstractDelegateFactory delegateFactory) {
    mClusterConfig = Preconditions.checkNotNull(clusterConfig, "clusterConfig");
    mConsensusContext = Preconditions.checkNotNull(consensusContext, "consensusContext");
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

  void transition(@Nonnull final Class<? extends State> newState) {
    mDelegate.off();
    mDelegate = mStateFactory.getDelegate(this, newState);
    mDelegate.on();
  }

  /* package-private */ ConsensusContext getContext() {
    return mConsensusContext;
  }

  /* package-private */ ClusterConfig getClusterConfig() {
    return mClusterConfig;
  }
}
