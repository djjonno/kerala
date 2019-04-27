package org.elkd.core.consensus

import com.google.common.annotations.VisibleForTesting
import io.grpc.stub.StreamObserver
import org.elkd.core.consensus.messages.*
import org.elkd.shared.annotations.Mockable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * RaftDelegator delegates messages to the correct internal raft state [follower, candidate, leader]
 * It also provides a transition mechanism to such states in order move between states at their
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
class RaftDelegator(private val stateFactory: AbstractStateFactory,
                    private val transitionRequirements: List<TransitionRequirement> = emptyList(),
                    @VisibleForTesting private val serialExecutor: ExecutorService = Executors.newSingleThreadExecutor()) : RaftDelegate {

  /**
   * Raft state is the internal state representation. Using the state design pattern,
   * our Raft implementation appears the same externally but internally switches
   * between the various follower/candidate/leader states.
   */
  private var delegate: RaftState? = null

  @JvmOverloads
  fun transition(state: State,
                 preHook: () -> Unit = {},
                 postHook: () -> Unit = {}) {
    serialOperation {
      delegate?.off()
      preHook()
      delegate = stateFactory.getState(state)
      delegate?.on()
      postHook()
    }
  }

  /* ---- Message Delegation ---- */

  /**
   * Delegate appendEntries to internal state handler for processing.
   */
  override fun delegateAppendEntries(request: AppendEntriesRequest,
                                     stream: StreamObserver<AppendEntriesResponse>) {
    serialOperation {
      evaluateTransitionRequirements(request) {
        delegate?.delegateAppendEntries(request, stream)
      }
    }
  }

  /**
   * Delegate requestVote to internal state handler for processing.
   */
  override fun delegateRequestVote(request: RequestVoteRequest,
                                   stream: StreamObserver<RequestVoteResponse>) {
    serialOperation {
      evaluateTransitionRequirements(request) {
        delegate?.delegateRequestVote(request, stream)
      }
    }
  }

  private fun serialOperation(block: () -> Unit) {
    serialExecutor.execute(block)
  }

  private fun evaluateTransitionRequirements(request: Request, block: () -> Unit) {
    transitionRequirements.forEach { req ->
      if (req.isTransitionRequired(request)) {
        transition(req.transitionTo, { req.transitionPreHook(request) }, { req.transitionPostHook(request) })
        return@forEach
      }
    }

    block()
  }
}
