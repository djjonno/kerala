package org.elkd.core.raft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.config.Config;
import org.elkd.core.raft.messages.AppendEntriesRequest;
import org.elkd.core.raft.messages.AppendEntriesResponse;
import org.elkd.core.raft.messages.Entry;
import org.elkd.core.raft.messages.RequestVoteRequest;
import org.elkd.core.raft.messages.RequestVoteResponse;
import org.elkd.core.log.Log;
import org.elkd.core.log.LogCommandExecutor;
import org.elkd.core.log.LogProvider;
import org.elkd.core.server.cluster.ClusterMessenger;
import org.elkd.core.server.cluster.ClusterSet;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Raft module performs consensus of the log over the cluster, using the {@link ClusterMessenger}
 * as a means of communication. This module registers as a delegate to the {@link org.elkd.core.server.cluster.ClusterService}
 * and acts as a state machine between the various raft states.
 *
 * @see <a href="https://raft.github.io/raft.pdf">https://raft.github.io/raft.pdf</a>
 * @see RaftFollowerDelegate
 * @see RaftCandidateDelegate
 * @see RaftLeaderDelegate
 */
public class Raft implements RaftDelegate {
  private static final Logger LOG = Logger.getLogger(Raft.class);

  private final Config mConfig;
  private final ClusterMessenger mClusterMessenger;
  private final LogProvider<Entry> mLogProvider;
  private final RaftContext mRaftContext;
  private final AbstractDelegateFactory mStateFactory;

  /**
   * This component provides objects (shared state) to the underlying raft delegates.
   * In order to simply synchronization, we are using a single-threaded executor
   * to guarantee serialization of all operations over the shared state.
   *
   * All underlying delegation within this component is single-threaded and synchronous.
   */
  private final ExecutorService mSerialExecutor;

  private RaftState mRaftState;

  public Raft(@Nonnull final Config config,
              @Nonnull final ClusterMessenger clusterMessenger,
              @Nonnull final RaftContext raftContext,
              @Nonnull final AbstractDelegateFactory delegateFactory,
              @Nonnull final LogProvider<Entry> log) {
    this(config, clusterMessenger, raftContext, delegateFactory, log, Executors.newSingleThreadExecutor());
  }

  @VisibleForTesting
  Raft(@Nonnull final Config config,
       @Nonnull final ClusterMessenger clusterMessenger,
       @Nonnull final RaftContext raftContext,
       @Nonnull final AbstractDelegateFactory delegateFactory,
       @Nonnull final LogProvider<Entry> log,
       @Nonnull final ExecutorService executorService) {
    mConfig = Preconditions.checkNotNull(config, "config");
    mClusterMessenger = Preconditions.checkNotNull(clusterMessenger, "clusterMessenger");
    mRaftContext = Preconditions.checkNotNull(raftContext, "raftContext");
    mStateFactory = Preconditions.checkNotNull(delegateFactory, "delegateFactory");
    mSerialExecutor = Preconditions.checkNotNull(executorService, "executorService");
    mLogProvider = Preconditions.checkNotNull(log, "log");
  }

  public void initialize() {
    LOG.info("initializing raft");
    mSerialExecutor.execute(() -> {
      mRaftState = mStateFactory.getInitialDelegate(this);
      mRaftState.on();
    });
  }

  public void delegateAppendEntries(final AppendEntriesRequest appendEntriesRequest,
                                    final StreamObserver<AppendEntriesResponse> responseObserver) {
    /* perform state-agnostic logic */
    termCheck(appendEntriesRequest.getTerm());

    mSerialExecutor.execute(() -> {
      mRaftState.delegateAppendEntries(appendEntriesRequest, responseObserver);
    });
  }

  public void delegateRequestVote(final RequestVoteRequest requestVoteRequest,
                                  final StreamObserver<RequestVoteResponse> responseObserver) {
    /* perform state-agnostic logic */
    termCheck(requestVoteRequest.getTerm());

    mSerialExecutor.execute(() -> {
      mRaftState.delegateRequestVote(requestVoteRequest, responseObserver);
    });
  }

  /* package */ void transition(@Nonnull final Class<? extends RaftState> nextState) {
    mSerialExecutor.execute(() -> {
      mRaftState.off();
      mRaftState = mStateFactory.getDelegate(this, nextState);
      mRaftState.on();
    });
  }

  /* package */ Config getConfig() {
    return mConfig;
  }

  /* package */ Log<Entry> getLog() {
    return mLogProvider.getLog();
  }

  /* package */ LogCommandExecutor<Entry> getLogCommandExecutor() {
    return mLogProvider.logCommandExecutor();
  }

  /* package */ ClusterSet getClusterSet() {
    return mClusterMessenger.getClusterSet();
  }

  /* package */ ClusterMessenger getClusterMessenger() {
    return mClusterMessenger;
  }

  /* package */ RaftContext getRaftContext() {
    return mRaftContext;
  }

  private void termCheck(final int requestTerm) {
    final RaftContext raftContext = getRaftContext();
    if (requestTerm > raftContext.getCurrentTerm()) {
      raftContext.setCurrentTerm(requestTerm);
      raftContext.setVotedFor(null);
      transition(RaftFollowerDelegate.class);
    }
  }
}
