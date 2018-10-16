package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.elkd.core.cluster.ClusterConfig;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVotesRequest;
import org.elkd.core.consensus.messages.RequestVotesResponse;
import org.elkd.core.log.Entry;
import org.elkd.core.log.LogInvoker;

import javax.annotation.Nonnull;

public class Consensus {
  private Delegate mDelegate;
  private final LogInvoker<Entry> mLogInvoker;
  private final ClusterConfig mClusterConfig;
  private final NodeState mNodeState;
  private final AbstractDelegateFactory mStateFactory;

  public Consensus(@Nonnull final LogInvoker<Entry> logInvoker,
                   @Nonnull final ClusterConfig clusterConfig,
                   @Nonnull final NodeState nodeState,
                   @Nonnull final AbstractDelegateFactory delegateFactory) {
    mLogInvoker = Preconditions.checkNotNull(logInvoker, "logInvoker");
    mClusterConfig = Preconditions.checkNotNull(clusterConfig, "clusterConfig");
    mNodeState = Preconditions.checkNotNull(nodeState, "nodeState");
    mStateFactory = Preconditions.checkNotNull(delegateFactory, "delegateFactory");
  }

  public void initialize() {
    mDelegate = mStateFactory.getInitialDelegate(this);
    mDelegate.on();
  }

  public AppendEntriesResponse delegateAppendEntries(final AppendEntriesRequest request,
                                                     final StreamObserver<AppendEntriesResponse> response) {
    return mDelegate.delegateAppendEntries(request, response);
  }

  public RequestVotesResponse delegateRequestVotes(final RequestVotesRequest request,
                                                   final StreamObserver<RequestVotesResponse> response) {
    return mDelegate.delegateRequestVotes(request, response);
  }

  void transition(@Nonnull final Class<? extends Delegate> newDelegate) {
    mDelegate.off();
    mDelegate = mStateFactory.getDelegate(this, newDelegate);
    mDelegate.on();
  }

  /* package-private */ LogInvoker<Entry> getLogInvoker() {
    return mLogInvoker;
  }

  /* package-private */ NodeState getContext() {
    return mNodeState;
  }

  /* package-private */ ClusterConfig getClusterConfig() {
    return mClusterConfig;
  }
}
