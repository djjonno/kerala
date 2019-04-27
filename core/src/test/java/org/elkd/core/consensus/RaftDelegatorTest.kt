package org.elkd.core.consensus

import com.nhaarman.mockitokotlin2.*
import io.grpc.stub.StreamObserver
import org.elkd.core.consensus.messages.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.ExecutorService

class RaftDelegatorTest {

  @Mock lateinit var appendEntriesRequest: AppendEntriesRequest
  @Mock lateinit var requestVoteRequest: RequestVoteRequest
  @Mock lateinit var appendEntriesResponseStream: StreamObserver<AppendEntriesResponse>
  @Mock lateinit var requestVoteResponseStream: StreamObserver<RequestVoteResponse>

  @Mock lateinit var stateFactory: AbstractStateFactory
  @Mock lateinit var followerState: RaftState
  @Mock lateinit var candidateState: RaftState
  @Mock lateinit var leaderState: RaftState
  @Mock lateinit var serialExecutor: ExecutorService

  @Mock lateinit var transitionRequirement: TransitionRequirement

  private lateinit var unitUnderTest: RaftDelegator

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    setupCommonExpectations()
    unitUnderTest = RaftDelegator(stateFactory,
        transitionRequirements = listOf(transitionRequirement),
        serialExecutor = serialExecutor)
  }

  private fun setupCommonExpectations() {
    doReturn(followerState)
        .`when`(stateFactory)
        .getState(State.FOLLOWER)

    doReturn(candidateState)
        .`when`(stateFactory)
        .getState(State.CANDIDATE)

    doReturn(leaderState)
        .`when`(stateFactory)
        .getState(State.LEADER)

    doAnswer { (it.arguments.first() as Runnable).run() }
        .`when`(serialExecutor)
        .execute(any())

    doReturn(false)
        .`when`(transitionRequirement)
        .isTransitionRequired(any())
  }

  @Test
  fun `should delegate appendEntries`() {
    // Given
    unitUnderTest.transition(State.FOLLOWER)

    // When
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStream)

    // Then
    verify(followerState).delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStream)
  }

  @Test
  fun `should delegate requestVote`() {
    // Given
    unitUnderTest.transition(State.FOLLOWER)

    // When
    unitUnderTest.delegateRequestVote(requestVoteRequest, requestVoteResponseStream)

    // Then
    verify(followerState).delegateRequestVote(requestVoteRequest, requestVoteResponseStream)
  }

  @Test
  fun `should perform serial transitions`() {
    // Given / When
    unitUnderTest.transition(State.FOLLOWER)
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStream)
    unitUnderTest.delegateRequestVote(requestVoteRequest, requestVoteResponseStream)
    unitUnderTest.transition(State.CANDIDATE)
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStream)
    unitUnderTest.delegateRequestVote(requestVoteRequest, requestVoteResponseStream)
    unitUnderTest.transition(State.LEADER)
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStream)
    unitUnderTest.delegateRequestVote(requestVoteRequest, requestVoteResponseStream)

    // Then
    val inOrder = inOrder(followerState, candidateState, leaderState)
    inOrder.verify(followerState).delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStream)
    inOrder.verify(followerState).delegateRequestVote(requestVoteRequest, requestVoteResponseStream)

    inOrder.verify(candidateState).delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStream)
    inOrder.verify(candidateState).delegateRequestVote(requestVoteRequest, requestVoteResponseStream)

    inOrder.verify(leaderState).delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStream)
    inOrder.verify(leaderState).delegateRequestVote(requestVoteRequest, requestVoteResponseStream)
  }

  @Test
  fun `should evaluate transition requirement`() {
    // Given / When
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStream)

    // Then
    verify(transitionRequirement).isTransitionRequired(appendEntriesRequest)
  }

  @Test
  fun `should perform transition when transition requirement is met`() {
    // Given
    val preHookRunnable = mock<Runnable>()
    val preHook: (request: Request) -> Unit = { preHookRunnable.run() }
    val postHookRunnable = mock<Runnable>()
    val postHook: (request: Request) -> Unit = { postHookRunnable.run() }
    doReturn(preHook)
        .`when`(transitionRequirement)
        .transitionPreHook
    doReturn(postHook)
        .`when`(transitionRequirement)
        .transitionPostHook
    doReturn(true)
        .`when`(transitionRequirement)
        .isTransitionRequired(any())
    doReturn(State.CANDIDATE)
        .`when`(transitionRequirement)
        .transitionTo
    unitUnderTest.transition(State.FOLLOWER)

    // When
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStream)

    // Then
    val inOrder = inOrder(preHookRunnable, postHookRunnable, followerState, candidateState)
    inOrder.verify(followerState).off()
    inOrder.verify(preHookRunnable).run()
    inOrder.verify(candidateState).on()
    inOrder.verify(postHookRunnable).run()
  }
}
