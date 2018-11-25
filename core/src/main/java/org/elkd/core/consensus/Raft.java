package org.elkd.core.consensus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.cluster.ClusterConnectionPool;
import org.elkd.core.cluster.ClusterSet;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.log.LogInvoker;

import javax.annotation.Nonnull;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class Raft implements RaftDelegate {
  private static final Logger LOG = Logger.getLogger(Raft.class);

  private final LogInvoker<Entry> mReplicatedLog;
  private final ClusterSet mClusterSet;
  private final ClusterConnectionPool mClusterConnectionPool;
  private final NodeState mNodeState;
  private final AbstractStateFactory mStateFactory;
  private final BlockingQueue<Class<? extends RaftState>> mTransitions = new LinkedBlockingDeque<>();
  private final ExecutorService mExecutorService;
  private final Object mLock = new Object();

  /* guarded by mLock */
  private RaftState mRaftState;

  public Raft(@Nonnull final LogInvoker<Entry> replicatedLog,
              @Nonnull final ClusterSet clusterSet,
              @Nonnull final NodeState nodeState,
              @Nonnull final AbstractStateFactory stateFactory) {
    this(replicatedLog, clusterSet, nodeState, stateFactory, Executors.newSingleThreadExecutor());
  }

  @VisibleForTesting
  Raft(@Nonnull final LogInvoker<Entry> replicatedLog,
       @Nonnull final ClusterSet clusterSet,
       @Nonnull final NodeState nodeState,
       @Nonnull final AbstractStateFactory delegateFactory,
       @Nonnull final ExecutorService executorService) {
    mReplicatedLog = Preconditions.checkNotNull(replicatedLog, "replicatedLog");
    mClusterSet = Preconditions.checkNotNull(clusterSet, "clusterSet");
    mNodeState = Preconditions.checkNotNull(nodeState, "nodeState");
    mStateFactory = Preconditions.checkNotNull(delegateFactory, "delegateFactory");
    mExecutorService = Preconditions.checkNotNull(executorService, "executorService");

    mClusterConnectionPool = new ClusterConnectionPool(mClusterSet);
  }

  public void initialize() {
    LOG.info("initializing raft");

    mClusterConnectionPool.initialize();

    synchronized (mLock) {
      mRaftState = mStateFactory.getInitialDelegate(this);
      mRaftState.on();
      mExecutorService.submit(this::performTransition);
    }
  }

  public void delegateAppendEntries(final AppendEntriesRequest appendEntriesRequest,
                                    final StreamObserver<AppendEntriesResponse> responseObserver) {
    synchronized (mLock) {
      LOG.info("delegating appendEntries to " + mRaftState);
      LOG.info("request: " + appendEntriesRequest);
      mRaftState.delegateAppendEntries(appendEntriesRequest, responseObserver);
    }
  }

  public void delegateRequestVote(final RequestVoteRequest requestVoteRequest,
                                  final StreamObserver<RequestVoteResponse> responseObserver) {
    synchronized (mLock) {
      mRaftState.delegateRequestVote(requestVoteRequest, responseObserver);
    }
  }

  void transition(@Nonnull final Class<? extends RaftState> newDelegate) {
    mTransitions.add(newDelegate);
  }

  /* package */ LogInvoker<Entry> getReplicatedLog() {
    return mReplicatedLog;
  }

  /* package */ ClusterSet getClusterSet() {
    return mClusterSet;
  }

  /* package */ ClusterConnectionPool getClusterConnectionPool() {
    return mClusterConnectionPool;
  }

  /* package */ NodeState getNodeState() {
    return mNodeState;
  }

  private void performTransition() {
    for (;;) {
      try {
        LOG.info("awaiting next transition");
        final Class<? extends RaftState> next = mTransitions.take();
        LOG.info("transitioning to -> " + next);
        synchronized (mLock) {
          mRaftState.off();
          mRaftState = mStateFactory.getDelegate(this, next);
          mRaftState.on();
        }
      } catch (final InterruptedException e) {
        LOG.error("failed to retrieve next transition", e);
      }
    }
  }
}
