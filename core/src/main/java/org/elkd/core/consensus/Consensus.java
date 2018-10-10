package org.elkd.core.consensus;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class Consensus {
  private State mState;
  private ConsensusContext mConsensusContext;
  private AbstractStateFactory mStateFactory;

  public Consensus(@Nonnull final ConsensusContext consensusContext,
                   @Nonnull final AbstractStateFactory abstractStateFactory) {
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

  ConsensusContext getContext() {
    return mConsensusContext;
  }
}
