package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import org.elkd.core.cluster.ClusterConfig;

import javax.annotation.Nonnull;

public class Consensus {
  private State mState;
  private ClusterConfig mClusterConfig;
  private ConsensusContext mConsensusContext;
  private AbstractStateFactory mStateFactory;

  public Consensus(@Nonnull final ClusterConfig clusterConfig,
                   @Nonnull final ConsensusContext consensusContext,
                   @Nonnull final AbstractStateFactory abstractStateFactory) {
    mClusterConfig = Preconditions.checkNotNull(clusterConfig, "clusterConfig");
    mConsensusContext = Preconditions.checkNotNull(consensusContext, "consensusContext");
    mStateFactory = Preconditions.checkNotNull(abstractStateFactory, "abstractStateFactory");
  }

  public void initialize() {
    mState = mStateFactory.getInitialState(this);
    mState.on();
  }

  public AppendEntriesResponse routeAppendEntries(final AppendEntriesRequest request) {
    return mState.handleAppendEntries(request);
  }

  public RequestVotesResponse routeRequestVotes(final RequestVotesRequest request) {
    return mState.handleRequestVotes(request);
  }

  void transition(@Nonnull final Class<? extends State> newState) {
    mState.off();
    mState = mStateFactory.getState(this, newState);
    mState.on();
  }

  /* package-private */ ConsensusContext getContext() {
    return mConsensusContext;
  }

  /* package-private */ ClusterConfig getClusterConfig() {
    return mClusterConfig;
  }
}
