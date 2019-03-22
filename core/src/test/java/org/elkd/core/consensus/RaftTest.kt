package org.elkd.core.consensus

import io.grpc.stub.StreamObserver
import org.elkd.core.config.Config
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.elkd.core.log.Log
import org.elkd.core.log.LogProvider
import org.elkd.core.server.cluster.ClusterSet
import org.elkd.core.testutil.Executors
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import java.util.concurrent.ExecutorService

import org.junit.Assert.assertSame
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.*

class RaftTest {
  @Mock internal lateinit var mConfig: Config
  @Mock internal lateinit var mRaft1: RaftState
  @Mock internal lateinit var mRaft2: RaftState
  @Mock internal lateinit var mRaftContext: RaftContext
  @Mock internal lateinit var mClusterSet: ClusterSet
  @Mock internal lateinit var mLogProvider: LogProvider<Entry>
  @Mock internal lateinit var mLog: Log<Entry>
  @Mock internal lateinit var mStateFactory: AbstractStateFactory
  @Mock internal lateinit var mRequestVoteRequest: RequestVoteRequest
  @Mock internal lateinit var mAppendEntriesRequest: AppendEntriesRequest
  @Mock internal lateinit var mRequestVotesResponseObserver: StreamObserver<RequestVoteResponse>
  @Mock internal lateinit var mAppendEntriesResponseObserver: StreamObserver<AppendEntriesResponse>

  private lateinit var mUnitUnderTest: Raft
  private lateinit var mExecutorService: ExecutorService

  @Before
  @Throws(Exception::class)
  fun setup() {
    MockitoAnnotations.initMocks(this)

    mExecutorService = Executors.getMockedSerialExecutor()

    setupCommonExpectations()

    mUnitUnderTest = Raft(
        mConfig,
        mClusterSet,
        mRaftContext,
        mStateFactory,
        mLogProvider,
        mExecutorService
    )
  }

  private fun setupCommonExpectations() {
    doReturn(mRaft1)
        .`when`<AbstractStateFactory>(mStateFactory)
        .getInitialDelegate(any())
    doReturn(mRaft2)
        .`when`<AbstractStateFactory>(mStateFactory)
        .getDelegate(any(), any())
    doReturn(mLog)
        .`when`<LogProvider<Entry>>(mLogProvider)
        .log
  }

  @Test
  @Throws(InterruptedException::class)
  fun should_get_initial_state_and_activate_on_initialize() {
    // Given / When
    mUnitUnderTest.initialize()

    // Then
    verify<AbstractStateFactory>(mStateFactory).getInitialDelegate(mUnitUnderTest)
    verify<RaftState>(mRaft1).on()
  }

  @Test
  @Throws(InterruptedException::class)
  fun should_transition_state_for_transition() {
    // Given
    mUnitUnderTest.initialize()
    val state = RaftLeaderDelegate::class.java

    // When
    mUnitUnderTest.transitionToState(state)

    // Then
    verify<RaftState>(mRaft1).off()
    verify<AbstractStateFactory>(mStateFactory).getDelegate(mUnitUnderTest, state)
    verify<RaftState>(mRaft2).on()
  }

  @Test
  @Throws(InterruptedException::class)
  fun should_route_appendEntries_to_active_state() {
    // Given
    mUnitUnderTest.initialize()

    // When
    mUnitUnderTest.delegateAppendEntries(mAppendEntriesRequest, mAppendEntriesResponseObserver)

    // Then
    verify<RaftState>(mRaft1).delegateAppendEntries(mAppendEntriesRequest, mAppendEntriesResponseObserver)
  }

  @Test
  @Throws(InterruptedException::class)
  fun should_route_requestVotes_to_active_state() {
    // Given
    mUnitUnderTest.initialize()

    // When
    mUnitUnderTest.delegateRequestVote(mRequestVoteRequest, mRequestVotesResponseObserver)

    // Then
    verify<RaftState>(mRaft1).delegateRequestVote(mRequestVoteRequest, mRequestVotesResponseObserver)
  }

  @Test
  fun should_get_config() {
    // Given / When - mUnitUnderTest

    // Then
    assertSame(mConfig, mUnitUnderTest.config)
  }

  @Test
  fun should_get_context() {
    // Given / When - mUnitUnderTest

    // Then
    assertSame(mRaftContext, mUnitUnderTest.raftContext)
  }

  @Test
  fun should_get_clusterSet() {
    // Given / When - mUnitUnderTest

    // Then
    assertSame(mClusterSet, mUnitUnderTest.clusterSet)
  }

  @Test
  fun should_transition_to_follower_if_term_gt_currentTerm_when_appendEntries() {
    // Given
    mUnitUnderTest.initialize()
    val currentTerm = 0
    doReturn(currentTerm)
        .`when`<RaftContext>(mRaftContext)
        .currentTerm
    val newTerm = currentTerm + 1
    doReturn(newTerm)
        .`when`<AppendEntriesRequest>(mAppendEntriesRequest)
        .term
    val followerDelegate = mock(RaftFollowerDelegate::class.java)
    doReturn(followerDelegate)
        .`when`<AbstractStateFactory>(mStateFactory)
        .getDelegate(any(), eq(RaftFollowerDelegate::class.java))

    // When
    mUnitUnderTest.delegateAppendEntries(mAppendEntriesRequest, mAppendEntriesResponseObserver)

    // Then
    verify<AbstractStateFactory>(mStateFactory).getDelegate(eq<Raft>(mUnitUnderTest), eq(RaftFollowerDelegate::class.java))
    verify<RaftContext>(mRaftContext).currentTerm = newTerm
    verify<RaftContext>(mRaftContext).votedFor = null
    verify(followerDelegate).on()
    verify(followerDelegate).delegateAppendEntries(mAppendEntriesRequest, mAppendEntriesResponseObserver)
  }

  @Test
  fun should_transition_to_follower_if_term_gt_currentTerm_when_requestVotes() {
    // Given
    mUnitUnderTest.initialize()
    val currentTerm = 0
    doReturn(currentTerm)
        .`when`<RaftContext>(mRaftContext)
        .currentTerm
    val newTerm = currentTerm + 1
    doReturn(newTerm)
        .`when`<RequestVoteRequest>(mRequestVoteRequest)
        .term
    val followerDelegate = mock(RaftFollowerDelegate::class.java)
    doReturn(followerDelegate)
        .`when`<AbstractStateFactory>(mStateFactory)
        .getDelegate(any(), eq(RaftFollowerDelegate::class.java))

    // When
    mUnitUnderTest.delegateRequestVote(mRequestVoteRequest, mRequestVotesResponseObserver)

    // Then
    verify<AbstractStateFactory>(mStateFactory).getDelegate(eq<Raft>(mUnitUnderTest), eq(RaftFollowerDelegate::class.java))
    verify<RaftContext>(mRaftContext).currentTerm = newTerm
    verify<RaftContext>(mRaftContext).votedFor = null
    verify(followerDelegate).on()
    verify(followerDelegate).delegateRequestVote(mRequestVoteRequest, mRequestVotesResponseObserver)
  }
}
