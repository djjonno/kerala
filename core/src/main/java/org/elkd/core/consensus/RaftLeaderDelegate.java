package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.server.cluster.ClusterMessenger;
import org.elkd.core.statemachine.SetStateMachineCommand;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;

class RaftLeaderDelegate implements RaftState {
  private static final Logger LOG = Logger.getLogger(RaftLeaderDelegate.class.getName());
  private static final int TIMEOUT_MS = 1000;

  private Timer mMonitor;

  private final Raft mRaft;
  private final ClusterMessenger mClusterMessenger;

  /* package */ RaftLeaderDelegate(@Nonnull final Raft raft,
                                   @Nonnull final ClusterMessenger clusterMessenger) {
    mRaft = Preconditions.checkNotNull(raft, "raft");
    mClusterMessenger = clusterMessenger;
  }

  @Override
  public void on() {
    LOG.info("ready");

    /* DEMO CODE */
    mRaft.getClusterConnectionPool().iterator().forEachRemaining(node -> {
      final AppendEntriesRequest request = AppendEntriesRequest
          .builder(0, 0, 0, mRaft.getClusterSet().getSelfNode().getId(), 0)
          .withEntry(Entry.builder("math").withCommand(new SetStateMachineCommand("x", "5")).build())
          .build();
      final ListenableFuture<AppendEntriesResponse> future = mClusterMessenger.appendEntries(node, request);
      try {
        LOG.info(future.get());
      } catch (Exception e) { }

      final RequestVoteRequest request2 = RequestVoteRequest
          .builder(0, mRaft.getClusterSet().getSelfNode().getId(), 0, 0)
          .build();
      final ListenableFuture<RequestVoteResponse> future2 = mClusterMessenger.requestVote(node, request2);
      try {
        LOG.info(future2.get());
      } catch (Exception e) { }
    });

    restartMonitor();
  }

  @Override
  public void off() {
    LOG.info("offline");
    stopMonitor();
  }

  @Override
  public void delegateAppendEntries(final AppendEntriesRequest appendEntriesRequest,
                                    final StreamObserver<AppendEntriesResponse> responseObserver) {
    responseObserver.onNext(AppendEntriesResponse.builder(102, true).build());
    responseObserver.onCompleted();
    restartMonitor();
  }

  @Override
  public void delegateRequestVote(final RequestVoteRequest requestVoteRequest,
                                  final StreamObserver<RequestVoteResponse> responseObserver) {
    responseObserver.onNext(RequestVoteResponse.builder(0, true).build());
    responseObserver.onCompleted();
    restartMonitor();
  }

  private void restartMonitor() {
    stopMonitor();
    mMonitor = new Timer(false);
    mMonitor.schedule(new TimerTask() {
      @Override
      public void run() {
        mRaft.transition(RaftFollowerDelegate.class);
      }
    }, TIMEOUT_MS, TIMEOUT_MS);
  }

  private void stopMonitor() {
    if (mMonitor != null) {
      mMonitor.cancel();
    }
  }
}
