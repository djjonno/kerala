package org.elkd.core.consensus

import com.google.common.annotations.VisibleForTesting
import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.Request
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.elkd.core.consensus.states.RaftState
import org.elkd.core.consensus.states.RaftStateFactory
import org.elkd.core.consensus.states.State
import org.elkd.core.runtime.NotificationsHub
import org.elkd.shared.annotations.Mockable
import java.util.concurrent.ExecutorService

/**
 * RaftDelegator delegates model to the correct internal raft state [follower, candidate, leader]
 * It also provides a transition mechanism to such states in order to move between states at their
 * own discretion.
 *
 *
 * Message Delegation
 * ==================
 * While we usually let all messages proceed to the internal handler,
 * sometimes it makes sense to handle message in this component in a
 * global sense, when necessary.  This helps to reduce the code duping
 * and downstream complexity.
 *
 * Threading
 * =========
 * To simplify the synchronization, both state transitions and message
 * delegation are performed in the same single-threaded executor.  This
 * way, we can essentially automatically ensure that the state transitions
 * and the message delivery is atomic.
 * @see serialDispatcher
 */
@Mockable
class RaftDelegator(
    private val stateFactory: RaftStateFactory,
    private val transitionContracts: List<TransitionContract> = emptyList(),
    @VisibleForTesting private val serialExecutor: ExecutorService = Pools.consensusPool
) : RaftDelegate {

  /**
   * Raft state is the internal state representation. Using the state design pattern,
   * our Raft implementation appears the same externally but internally switches
   * between the various follower/candidate/leader states.
   */
  private var delegate: RaftState? = null

  fun initialize(state: State) {
    serialOperation {
      delegate = stateFactory.getState(state).apply { on() }
    }
  }

  /**
   * Async transition command, schedules the transition for execution
   * at some point in the future.
   */
  fun transitionRequest(
      state: State,
      preHook: () -> Unit = {},
      postHook: () -> Unit = {}
  ) {
    serialOperation {
      transition(state, preHook, postHook)
    }
  }

  private fun transition(
      state: State,
      preHook: () -> Unit = {},
      postHook: () -> Unit = {}
  ) {
    val oldDelegate = delegate
    val newDelegate = stateFactory.getState(state)
    delegate?.off()
    preHook()
    delegate = newDelegate.apply { on() }
    postHook()
    LOGGER.info("-> $state")
    if (oldDelegate != newDelegate) {
      NotificationsHub.pub(NotificationsHub.Channel.CONSENSUS_CHANGE)
    }
  }

  override val supportedOps: Set<OpCategory>
    get() = delegate?.supportedOps ?: emptySet()

  /* ---- Message Delegation ---- */

  /**
   * Delegate appendEntries to internal state handler for processing.
   */
  override fun delegateAppendEntries(
      request: AppendEntriesRequest,
      stream: StreamObserver<AppendEntriesResponse>
  ) {
    serialOperation {
      evaluateTransitionRequirements(request) {
        delegate?.delegateAppendEntries(request, stream)
      }
    }
  }

  /**
   * Delegate requestVote to internal state handler for processing.
   */
  override fun delegateRequestVote(
      request: RequestVoteRequest,
      stream: StreamObserver<RequestVoteResponse>
  ) {
    serialOperation {
      evaluateTransitionRequirements(request) {
        delegate?.delegateRequestVote(request, stream)
      }
    }
  }

  private fun serialOperation(block: () -> Unit) {
    serialExecutor.submit(block)
  }

  private fun evaluateTransitionRequirements(request: Request, block: () -> Unit) {
    val req = transitionContracts.firstOrNull { it.isTransitionRequired(request) }

    if (req != null) {
      transition(req.transitionTo, { req.transitionPreHook(request) }, {
        req.transitionPostHook(request)
        block()
      })
    } else {
      block()
    }
  }

  private companion object {
    var LOGGER: Logger = Logger.getLogger(RaftDelegator::class.java)
  }
}
