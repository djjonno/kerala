package org.elkd.core.consensus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.config.Config;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.log.LogChangeReason;
import org.elkd.core.log.commands.AppendFromCommand;
import org.elkd.core.log.commands.CommitCommand;

import javax.annotation.Nonnull;

class RaftFollowerDelegate implements RaftState {
  private static final Logger LOG = Logger.getLogger(RaftFollowerDelegate.class.getName());

  private final Raft mRaft;

  private TimeoutMonitor mTimeoutMonitor;

  RaftFollowerDelegate(@Nonnull final Raft raft) {
    this(
        raft,
        new TimeoutMonitor(
            raft.getConfig().getAsInteger(Config.KEY_RAFT_ELECTION_TIMEOUT_MS),
            () -> raft.transitionToState(RaftCandidateDelegate.class)
        )
    );
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
    try {
      validateAppendEntriesRequest(appendEntriesRequest);
      final AppendFromCommand<Entry> appendFromCommand = AppendFromCommand.Companion
          .build(appendEntriesRequest.getPrevLogIndex() + 1, appendEntriesRequest.getEntries(), LogChangeReason.REPLICATION);
      mRaft.getLogCommandExecutor().execute(appendFromCommand);

      if (appendEntriesRequest.getLeaderCommit() > mRaft.getLog().getCommitIndex()) {
        final long commitIndex = Math.min(appendEntriesRequest.getLeaderCommit(), mRaft.getLog().getLastIndex());
        mRaft.getLogCommandExecutor().execute(CommitCommand.build(commitIndex, LogChangeReason.REPLICATION));
      }
      replySuccess(responseObserver);
    } catch (final Exception e) {
      replyFalse(responseObserver);
    } finally {
      mTimeoutMonitor.reset();
    }
  }

  @Override
  public void delegateRequestVote(final RequestVoteRequest requestVoteRequest,
                                  final StreamObserver<RequestVoteResponse> responseObserver) {
    mTimeoutMonitor.reset();
    responseObserver.onCompleted();
  }

  private void replySuccess(final StreamObserver<AppendEntriesResponse> responseObserver) {
    responseObserver.onNext(AppendEntriesResponse.builder(mRaft.getRaftContext().getCurrentTerm(), true).build());
    responseObserver.onCompleted();
  }

  private void validateAppendEntriesRequest(final AppendEntriesRequest appendEntriesRequest) throws Exception {
    if (appendEntriesRequest.getTerm() < mRaft.getRaftContext().getCurrentTerm()) {
      throw new Exception("Term mismatch. Validation Failed.");
    }
    /* This check is only relevant when there are entries in the log.
       `prevLogIndex == -1` occurs for the first ever log entry.
     */
    if (appendEntriesRequest.getPrevLogIndex() > -1) {
      final Entry prevEntry = mRaft.getLog().read(appendEntriesRequest.getPrevLogIndex());
      if (prevEntry == null || (prevEntry.getTerm() != appendEntriesRequest.getPrevLogTerm())) {
        throw new Exception("Entry.term mismatch. Validation Failed.");
      }
    }
  }

  /*
   * Informs sender that the entries were not appended to the log.
   */
  private void replyFalse(final StreamObserver<AppendEntriesResponse> responseObserver) {
    responseObserver.onNext(AppendEntriesResponse.builder(mRaft.getRaftContext().getCurrentTerm(), false).build());
    responseObserver.onCompleted();
  }
}
