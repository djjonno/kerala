package org.elkd.core.consensus

import io.grpc.stub.StreamObserver
import org.elkd.core.config.Config
import org.elkd.core.log.Log
import org.elkd.core.log.LogProvider
import org.elkd.core.consensus.messages.*
import org.elkd.core.server.cluster.ClusterMessenger
import org.elkd.core.server.cluster.ClusterSet
import org.elkd.core.testutil.Executors
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.concurrent.ExecutorService

class RaftTest {
  @Mock internal lateinit var config: Config
  @Mock internal lateinit var raft1: RaftState
  @Mock internal lateinit var raft2: RaftState
  @Mock internal lateinit var raftContext: RaftContext
  @Mock internal lateinit var clusterSet: ClusterSet
  @Mock internal lateinit var clusterMessenger: ClusterMessenger
  @Mock internal lateinit var logProvider: LogProvider<Entry>
  @Mock internal lateinit var log: Log<Entry>
  @Mock internal lateinit var stateFactory: AbstractStateFactory
  @Mock internal lateinit var requestVoteRequest: RequestVoteRequest
  @Mock internal lateinit var appendEntriesRequest: AppendEntriesRequest
  @Mock internal lateinit var requestVotesResponseObserver: StreamObserver<RequestVoteResponse>
  @Mock internal lateinit var appendEntriesResponseObserver: StreamObserver<AppendEntriesResponse>

  private lateinit var unitUnderTest: Raft
  private lateinit var executorService: ExecutorService

  @Before
  @Throws(Exception::class)
  fun setup() {
    MockitoAnnotations.initMocks(this)

    executorService = Executors.getMockedSerialExecutor()

    setupCommonExpectations()

    unitUnderTest = Raft(
        config,
        clusterMessenger,
        raftContext,
        stateFactory,
        logProvider,
        executorService
    )
  }

  private fun setupCommonExpectations() {
    doReturn(clusterSet)
        .`when`(clusterMessenger)
        .clusterSet
    doReturn(raft1)
        .`when`<AbstractStateFactory>(stateFactory)
        .getInitialDelegate(any())
    doReturn(raft2)
        .`when`<AbstractStateFactory>(stateFactory)
        .getState(any(), any())
    doReturn(log)
        .`when`<LogProvider<Entry>>(logProvider)
        .log
  }

  @Test
  @Throws(InterruptedException::class)
  fun should_get_initial_state_and_activate_on_initialize() {
    // Given / When
    unitUnderTest.initialize()

    // Then
    verify<AbstractStateFactory>(stateFactory).getInitialDelegate(unitUnderTest)
    verify<RaftState>(raft1).on()
  }

  @Test
  @Throws(InterruptedException::class)
  fun should_transition_state_for_transition() {
    // Given
    unitUnderTest.initialize()
    val state = RaftLeaderState::class.java

    // When
    unitUnderTest.transition(state)

    // Then
    verify<RaftState>(raft1).off()
    verify<AbstractStateFactory>(stateFactory).getState(unitUnderTest, state)
    verify<RaftState>(raft2).on()
  }

  @Test
  @Throws(InterruptedException::class)
  fun should_route_appendEntries_to_active_state() {
    // Given
    unitUnderTest.initialize()

    // When
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseObserver)

    // Then
    verify<RaftState>(raft1).delegateAppendEntries(appendEntriesRequest, appendEntriesResponseObserver)
  }

  @Test
  @Throws(InterruptedException::class)
  fun should_route_requestVotes_to_active_state() {
    // Given
    unitUnderTest.initialize()

    // When
    unitUnderTest.delegateRequestVote(requestVoteRequest, requestVotesResponseObserver)

    // Then
    verify<RaftState>(raft1).delegateRequestVote(requestVoteRequest, requestVotesResponseObserver)
  }

  @Test
  fun should_get_config() {
    // Given / When - unitUnderTest

    // Then
    assertSame(config, unitUnderTest.config)
  }

  @Test
  fun should_get_context() {
    // Given / When - unitUnderTest

    // Then
    assertSame(raftContext, unitUnderTest.raftContext)
  }

  @Test
  fun should_get_clusterSet() {
    // Given / When - unitUnderTest

    // Then
    assertSame(clusterMessenger, unitUnderTest.clusterMessenger)
    assertSame(clusterMessenger.clusterSet, unitUnderTest.clusterSet)
  }

  @Test
  fun should_transition_to_follower_if_term_gt_currentTerm_when_appendEntries() {
    // Given
    unitUnderTest.initialize()
    val currentTerm = 0
    doReturn(currentTerm)
        .`when`<RaftContext>(raftContext)
        .currentTerm
    val newTerm = currentTerm + 1
    doReturn(newTerm)
        .`when`<AppendEntriesRequest>(appendEntriesRequest)
        .term
    val followerDelegate = mock(RaftFollowerState::class.java)
    doReturn(followerDelegate)
        .`when`<AbstractStateFactory>(stateFactory)
        .getState(any(), eq(RaftFollowerState::class.java))

    // When
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseObserver)

    // Then
    verify<AbstractStateFactory>(stateFactory).getState(eq<Raft>(unitUnderTest), eq(RaftFollowerState::class.java))
    verify<RaftContext>(raftContext).currentTerm = newTerm
    verify<RaftContext>(raftContext).votedFor = null
    verify(followerDelegate).on()
    verify(followerDelegate).delegateAppendEntries(appendEntriesRequest, appendEntriesResponseObserver)
  }

  @Test
  fun should_transition_to_follower_if_term_gt_currentTerm_when_requestVotes() {
    // Given
    unitUnderTest.initialize()
    val currentTerm = 0
    doReturn(currentTerm)
        .`when`<RaftContext>(raftContext)
        .currentTerm
    val newTerm = currentTerm + 1
    doReturn(newTerm)
        .`when`<RequestVoteRequest>(requestVoteRequest)
        .term
    val followerDelegate = mock(RaftFollowerState::class.java)
    doReturn(followerDelegate)
        .`when`<AbstractStateFactory>(stateFactory)
        .getState(any(), eq(RaftFollowerState::class.java))

    // When
    unitUnderTest.delegateRequestVote(requestVoteRequest, requestVotesResponseObserver)

    // Then
    verify<AbstractStateFactory>(stateFactory).getState(eq<Raft>(unitUnderTest), eq(RaftFollowerState::class.java))
    verify<RaftContext>(raftContext).currentTerm = newTerm
    verify<RaftContext>(raftContext).votedFor = null
    verify(followerDelegate).on()
    verify(followerDelegate).delegateRequestVote(requestVoteRequest, requestVotesResponseObserver)
  }
}
