package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

class RaftCandidateState implements RaftState {
  private static final Logger LOG = Logger.getLogger(RaftCandidateState.class.getName());

  private final Raft mRaft;

  RaftCandidateState(@Nonnull final Raft raft) {
    mRaft = Preconditions.checkNotNull(raft, "raft");
  }

  @Override
  public void on() {
    LOG.info("online");
  }

  @Override
  public void off() {
    LOG.info("offline");
  }

  @Override
  public void delegateAppendEntries(final AppendEntriesRequest appendEntriesRequest,
                                    final StreamObserver<AppendEntriesResponse> responseObserver) {
  }

  @Override
  public void delegateRequestVote(final RequestVoteRequest requestVoteRequest,
                                  final StreamObserver<RequestVoteResponse> responseObserver) {
  }
}
