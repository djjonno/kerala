package org.elkd.core.consensus

import com.google.common.annotations.VisibleForTesting
import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.elkd.core.log.Log
import org.elkd.core.log.LogCommandExecutor
import org.elkd.core.log.LogProvider
import org.elkd.core.server.cluster.ClusterMessenger
import org.elkd.core.server.cluster.ClusterSet
import org.elkd.shared.annotations.Mockable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Raft module performs consensus of the logProvider over the cluster, using the [ClusterMessenger]
 * as a means of communication. This module registers as a delegate to the [org.elkd.core.server.cluster.ClusterService]
 * and acts as a state machine between the various consensus states.
 *
 * @see [https://raft.github.io/raft.pdf](https://raft.github.io/raft.pdf)
 * @see RaftFollowerState
 * @see RaftCandidateState
 * @see RaftLeaderState
 */
@Mockable
class Raft @VisibleForTesting
internal constructor(val config: Config,
                     val clusterMessenger: ClusterMessenger,
                     val raftContext: RaftContext,
                     val stateFactory: AbstractStateFactory,
                     val logProvider: LogProvider<Entry>,
                     val serialExecutor: ExecutorService) : RaftDelegate {

  constructor(config: Config,
              clusterMessenger: ClusterMessenger,
              raftContext: RaftContext,
              stateFactory: AbstractStateFactory,
              logProvider: LogProvider<Entry>) : this(
      config, clusterMessenger, raftContext, stateFactory, logProvider,
      Executors.newSingleThreadExecutor(ThreadFactory.raftThreadFactory())) {
  }

  /**
   * Raft state is the internal state representation. Using the state design pattern,
   * our Raft implementation appears the same externally but internally its logic changes.
   */
  private var raftState: RaftState? = null

  val log: Log<Entry>
    get() = logProvider.log

  val logCommandExecutor: LogCommandExecutor<Entry>
    get() = logProvider.logCommandExecutor

  val clusterSet: ClusterSet
    get() = clusterMessenger.clusterSet

  fun initialize() {
    LOG.info("initializing consensus")
    serialExecutor.execute {
      raftState = stateFactory.getInitialDelegate(this)
      raftState?.on()
    }
  }

  override fun delegateAppendEntries(request: AppendEntriesRequest,
                                     responseObserver: StreamObserver<AppendEntriesResponse>) {
    /* perform state-agnostic logic */
    termCheck(request.term)

    serialExecutor.execute { raftState?.delegateAppendEntries(request, responseObserver) }
  }

  override fun delegateRequestVote(request: RequestVoteRequest,
                                   responseObserver: StreamObserver<RequestVoteResponse>) {
    /* perform state-agnostic logic */
    termCheck(request.term)

    serialExecutor.execute { raftState?.delegateRequestVote(request, responseObserver) }
  }

  fun transition(nextState: Class<out RaftState>) {
    serialExecutor.execute {
      raftState?.off()
      raftState = stateFactory.getState(this, nextState)
      raftState?.on()
    }
  }

  private fun termCheck(requestTerm: Int) {
    val raftContext = raftContext
    if (requestTerm > raftContext.currentTerm) {
      raftContext.currentTerm = requestTerm
      raftContext.votedFor = null
      transition(RaftFollowerState::class.java)
    }
  }

  companion object {
    private val LOG = Logger.getLogger(Raft::class.java)
  }
}
