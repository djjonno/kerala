package org.elkd.core.consensus;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class Consensus {
  private State mState;
  private Context mContext;
  private AbstractStateFactory mStateFactory;

  public Consensus(@Nonnull final Context context,
                   @Nonnull final AbstractStateFactory abstractStateFactory) {
    mContext = Preconditions.checkNotNull(context, "context");
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

  Context getContext() {
    return mContext;
  }
}
