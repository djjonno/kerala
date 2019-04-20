package org.elkd.core.consensus

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verify
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.runBlocking
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RaftDelegatorTest {

  @Mock lateinit var appendEntriesRequest: AppendEntriesRequest
  @Mock lateinit var requestVoteRequest: RequestVoteRequest
  @Mock lateinit var appendEntriesResponseStreamObserver: StreamObserver<AppendEntriesResponse>
  @Mock lateinit var requestVoteResponseStreamObserver: StreamObserver<RequestVoteResponse>

  @Mock lateinit var stateFactory: AbstractStateFactory
  @Mock lateinit var followerState: RaftState
  @Mock lateinit var candidateState: RaftState
  @Mock lateinit var leaderState: RaftState

  private lateinit var unitUnderTest: RaftDelegator

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    setupCommonExpectations()
    unitUnderTest = RaftDelegator(stateFactory)
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
  }

  @Test
  fun `should delegate appendEntries`() = runBlocking {
    // Given
    unitUnderTest.transition(State.FOLLOWER)

    // When
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStreamObserver)

    // Then
    verify(followerState).delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStreamObserver)
  }

  @Test
  fun `should delegate requestVote`() = runBlocking {
    // Given
    unitUnderTest.transition(State.FOLLOWER)

    // When
    unitUnderTest.delegateRequestVote(requestVoteRequest, requestVoteResponseStreamObserver)

    // Then
    verify(followerState).delegateRequestVote(requestVoteRequest, requestVoteResponseStreamObserver)
  }

  @Test
  fun `should perform serial transitions`() {
    // Given / When
    unitUnderTest.transition(State.FOLLOWER)
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStreamObserver)
    unitUnderTest.delegateRequestVote(requestVoteRequest, requestVoteResponseStreamObserver)
    unitUnderTest.transition(State.CANDIDATE)
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStreamObserver)
    unitUnderTest.delegateRequestVote(requestVoteRequest, requestVoteResponseStreamObserver)
    unitUnderTest.transition(State.LEADER)
    unitUnderTest.delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStreamObserver)
    unitUnderTest.delegateRequestVote(requestVoteRequest, requestVoteResponseStreamObserver)

    // Then
    val inOrder = inOrder(followerState, candidateState, leaderState)
    inOrder.verify(followerState).delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStreamObserver)
    inOrder.verify(followerState).delegateRequestVote(requestVoteRequest, requestVoteResponseStreamObserver)

    inOrder.verify(candidateState).delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStreamObserver)
    inOrder.verify(candidateState).delegateRequestVote(requestVoteRequest, requestVoteResponseStreamObserver)

    inOrder.verify(leaderState).delegateAppendEntries(appendEntriesRequest, appendEntriesResponseStreamObserver)
    inOrder.verify(leaderState).delegateRequestVote(requestVoteRequest, requestVoteResponseStreamObserver)
  }

  @Test
  fun `should evaluate transition requirement`() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  @Test
  fun `should perform transition when transition requirement is met`() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  @Test
  fun `should call transition requirement hooks correctly`() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
