package org.elkd.core.consensus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.config.Config;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;

import javax.annotation.Nonnull;

class RaftFollowerDelegate implements RaftState {
  private static final Logger LOG = Logger.getLogger(RaftFollowerDelegate.class.getName());

  private final Raft mRaft;

  private TimeoutMonitor mTimeoutMonitor;

  RaftFollowerDelegate(@Nonnull final Raft raft) {
    this(raft, new TimeoutMonitor(
        raft.getConfig().getAsInteger(Config.KEY_RAFT_ELECTION_TIMEOUT_MS),
        () -> {
          raft.transitionToState(RaftCandidateDelegate.class);
        }
    ));
  }

  @VisibleForTesting
  RaftFollowerDelegate(@Nonnull final Raft raft,
                       @Nonnull final TimeoutMonitor timeoutMonitor) {
    mRaft = Preconditions.checkNotNull(raft, "raft");
    mTimeoutMonitor = Preconditions.checkNotNull(timeoutMonitor, "timeoutMonitor");
  }

  @Override
  public void on() {
    LOG.info("ready");
    mTimeoutMonitor.reset();
  }

  @Override
  public void off() {
    LOG.info("offline");
    mTimeoutMonitor.stop();
  }

  @Override
  public void delegateAppendEntries(final AppendEntriesRequest appendEntriesRequest,
                                    final StreamObserver<AppendEntriesResponse> responseObserver) {
    responseObserver.onCompleted();
    mTimeoutMonitor.reset();
  }

  @Override
  public void delegateRequestVote(final RequestVoteRequest requestVoteRequest,
                                  final StreamObserver<RequestVoteResponse> responseObserver) {
    responseObserver.onCompleted();
    mTimeoutMonitor.reset();
  }
}
