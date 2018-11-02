package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.elkd.core.cluster.ClusterConfig;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVotesRequest;
import org.elkd.core.consensus.messages.RequestVotesResponse;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.LogInvoker;

import javax.annotation.Nonnull;

public class Raft implements RaftDelegate {
  private RaftState mRaftDelegate;
  private final LogInvoker<Entry> mReplicatedLog;
  private final ClusterConfig mClusterConfig;
  private final NodeState mNodeState;
  private final AbstractStateFactory mStateFactory;

  public Raft(@Nonnull final LogInvoker<Entry> replicatedLog,
              @Nonnull final ClusterConfig clusterConfig,
              @Nonnull final NodeState nodeState,
              @Nonnull final AbstractStateFactory delegateFactory) {
    mReplicatedLog = Preconditions.checkNotNull(replicatedLog, "replicatedLog");
    mClusterConfig = Preconditions.checkNotNull(clusterConfig, "clusterConfig");
    mNodeState = Preconditions.checkNotNull(nodeState, "nodeState");
    mStateFactory = Preconditions.checkNotNull(delegateFactory, "delegateFactory");
  }

  public void initialize() {
    mRaftDelegate = mStateFactory.getInitialDelegate(this);
    mRaftDelegate.on();
  }

  public void delegateAppendEntries(final AppendEntriesRequest appendEntriesRequest,
                                    final StreamObserver<AppendEntriesResponse> responseObserver) {
    mRaftDelegate.delegateAppendEntries(appendEntriesRequest, responseObserver);
  }

  public void delegateRequestVotes(final RequestVotesRequest requestVotesRequest,
                                   final StreamObserver<RequestVotesResponse> responseObserver) {
    mRaftDelegate.delegateRequestVotes(requestVotesRequest, responseObserver);
  }

  void transition(@Nonnull final Class<? extends RaftState> newDelegate) {
    mRaftDelegate.off();
    mRaftDelegate = mStateFactory.getDelegate(this, newDelegate);
    mRaftDelegate.on();
  }

  /* package */ LogInvoker<Entry> getReplicatedLog() {
    return mReplicatedLog;
  }

  /* package */ NodeState getNodeState() {
    return mNodeState;
  }

  /* package */ ClusterConfig getClusterConfig() {
    return mClusterConfig;
  }
}
