package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.server.cluster.ClusterMessenger;

import javax.annotation.Nonnull;

class RaftLeaderDelegate implements RaftState {
  private static final Logger LOG = Logger.getLogger(RaftLeaderDelegate.class.getName());

  private final Raft mRaft;
  private final ClusterMessenger mClusterMessenger;

  private LeaderContext mLeaderContext;

  /* package */ RaftLeaderDelegate(@Nonnull final Raft raft,
                                   @Nonnull final ClusterMessenger clusterMessenger) {
    mRaft = Preconditions.checkNotNull(raft, "raft");
    mClusterMessenger = clusterMessenger;
  }

  @Override
  public void on() {
    mLeaderContext = new LeaderContext(mRaft.getClusterSet().getNodes(), mRaft.getRaftContext());
    LOG.info("ready");
  }

  @Override
  public void off() {
    LOG.info("offline");
  }

  @Override
  public void delegateAppendEntries(final AppendEntriesRequest appendEntriesRequest,
                                    final StreamObserver<AppendEntriesResponse> responseObserver) {
    responseObserver.onCompleted();
  }

  @Override
  public void delegateRequestVote(final RequestVoteRequest requestVoteRequest,
                                  final StreamObserver<RequestVoteResponse> responseObserver) {
    responseObserver.onCompleted();
  }
}
