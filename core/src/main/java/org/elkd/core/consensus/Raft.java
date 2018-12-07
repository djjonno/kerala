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
import org.elkd.core.log.LogInvoker;
import org.elkd.core.server.cluster.ClusterConnectionPool;
import org.elkd.core.server.cluster.ClusterSet;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Raft implements RaftDelegate {
  private static final Logger LOG = Logger.getLogger(Raft.class);

  private final Config mConfig;
  private final ClusterSet mClusterSet;
  private final ClusterConnectionPool mClusterConnectionPool;
  private final LogInvoker<Entry> mLogInvoker;
  private final RaftContext mRaftContext;
  private final AbstractStateFactory mStateFactory;
  private final ExecutorService mSerialExecutor;

  private RaftState mRaftState;

  public Raft(@Nonnull final Config config,
              @Nonnull final ClusterSet clusterSet,
              @Nonnull final RaftContext raftContext,
              @Nonnull final AbstractStateFactory stateFactory) {
    this(config, clusterSet, raftContext, stateFactory, Executors.newSingleThreadExecutor());
  }

  @VisibleForTesting
  Raft(@Nonnull final Config config,
       @Nonnull final ClusterSet clusterSet,
       @Nonnull final RaftContext raftContext,
       @Nonnull final AbstractStateFactory delegateFactory,
       @Nonnull final ExecutorService executorService) {
    mConfig = Preconditions.checkNotNull(config, "config");
    mClusterSet = Preconditions.checkNotNull(clusterSet, "clusterSet");
    mRaftContext = Preconditions.checkNotNull(raftContext, "raftContext");
    mStateFactory = Preconditions.checkNotNull(delegateFactory, "delegateFactory");
    mSerialExecutor = Preconditions.checkNotNull(executorService, "executorService");
    mLogInvoker = mRaftContext.getLogInvoker();

    mClusterConnectionPool = new ClusterConnectionPool(mClusterSet);
  }

  public void initialize() {
    LOG.info("initializing raft");

    mClusterConnectionPool.initialize();

    mSerialExecutor.execute(() -> {
      mRaftState = mStateFactory.getInitialDelegate(this);
      mRaftState.on();
    });
  }

  public void delegateAppendEntries(final AppendEntriesRequest appendEntriesRequest,
                                    final StreamObserver<AppendEntriesResponse> responseObserver) {
    mSerialExecutor.execute(() -> {
      LOG.info("delegating appendEntries to " + mRaftState + " w/ " + appendEntriesRequest);
      mRaftState.delegateAppendEntries(appendEntriesRequest, responseObserver);
    });
  }

  public void delegateRequestVote(final RequestVoteRequest requestVoteRequest,
                                  final StreamObserver<RequestVoteResponse> responseObserver) {
    mSerialExecutor.execute(() -> {
      LOG.info("delegating appendEntries to " + mRaftState + " w/ " + requestVoteRequest);
      mRaftState.delegateRequestVote(requestVoteRequest, responseObserver);
    });
  }

  /* package */ void transitionToState(@Nonnull final Class<? extends RaftState> nextState) {
    LOG.info("transitioning to -> " + nextState);
    mSerialExecutor.execute(() -> {
      mRaftState.off();
      mRaftState = mStateFactory.getDelegate(this, nextState);
      mRaftState.on();
    });
  }

  /* package */ Config getConfig() {
    return mConfig;
  }

  /* package */ LogInvoker<Entry> getLogInvoker() {
    return mLogInvoker;
  }

  /* package */ ClusterSet getClusterSet() {
    return mClusterSet;
  }

  /* package */ ClusterConnectionPool getClusterConnectionPool() {
    return mClusterConnectionPool;
  }

  /* package */ RaftContext getRaftContext() {
    return mRaftContext;
  }
}
