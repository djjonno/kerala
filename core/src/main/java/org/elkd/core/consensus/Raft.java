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

public class Raft implements RaftDelegate {
  private RaftState mRaftDelegate;
  private final LogInvoker<Entry> mLogInvoker;
  private final ClusterConfig mClusterConfig;
  private final NodeState mNodeState;
  private final AbstractStateFactory mStateFactory;

  public Raft(@Nonnull final LogInvoker<Entry> logInvoker,
              @Nonnull final ClusterConfig clusterConfig,
              @Nonnull final NodeState nodeState,
              @Nonnull final AbstractStateFactory delegateFactory) {
    mLogInvoker = Preconditions.checkNotNull(logInvoker, "logInvoker");
    mClusterConfig = Preconditions.checkNotNull(clusterConfig, "clusterConfig");
    mNodeState = Preconditions.checkNotNull(nodeState, "nodeState");
    mStateFactory = Preconditions.checkNotNull(delegateFactory, "delegateFactory");
  }

  public void initialize() {
    mRaftDelegate = mStateFactory.getInitialDelegate(this);
    mRaftDelegate.on();
  }

  public void delegateAppendEntries(final AppendEntriesRequest request,
                                    final StreamObserver<AppendEntriesResponse> response) {
    mRaftDelegate.delegateAppendEntries(request, response);
  }

  public void delegateRequestVotes(final RequestVotesRequest request,
                                   final StreamObserver<RequestVotesResponse> response) {
    mRaftDelegate.delegateRequestVotes(request, response);
  }

  void transition(@Nonnull final Class<? extends RaftState> newDelegate) {
    mRaftDelegate.off();
    mRaftDelegate = mStateFactory.getDelegate(this, newDelegate);
    mRaftDelegate.on();
  }

  /* package */ LogInvoker<Entry> getLogInvoker() {
    return mLogInvoker;
  }

  /* package */ NodeState getNodeState() {
    return mNodeState;
  }

  /* package */ ClusterConfig getClusterConfig() {
    return mClusterConfig;
  }
}
