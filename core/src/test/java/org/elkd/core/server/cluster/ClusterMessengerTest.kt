package org.elkd.core.server.cluster

import com.google.common.util.concurrent.ListenableFuture
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import java.util.concurrent.ExecutionException
import kotlinx.coroutines.runBlocking
import org.elkd.core.ElkdRuntimeException
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.elkd.core.server.cluster.exceptions.NodeNotFoundException
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ClusterMessengerTest {
  @Mock lateinit var node: Node
  @Mock lateinit var channel: ClusterConnectionPool.Channel
  @Mock lateinit var clusterConnectionPool: ClusterConnectionPool
  @Mock lateinit var converterRegistry: ConverterRegistry

  @Mock lateinit var appendEntriesRequest: AppendEntriesRequest
  @Mock lateinit var appendEntriesResponse: AppendEntriesResponse
  @Mock lateinit var rpcAppendEntriesResponseFuture: ListenableFuture<RpcAppendEntriesResponse>
  private lateinit var rpcAppendEntriesRequest: RpcAppendEntriesRequest
  private lateinit var rpcAppendEntriesResponse: RpcAppendEntriesResponse

  @Mock lateinit var requestVoteRequest: RequestVoteRequest
  @Mock lateinit var requestVoteResponse: RequestVoteResponse
  @Mock lateinit var rpcRequestVoteResponseFuture: ListenableFuture<RpcRequestVoteRequest>
  private lateinit var rpcRequestVoteRequest: RpcRequestVoteRequest
  private lateinit var rpcRequestVoteResponse: RpcRequestVoteResponse

  private lateinit var unitUnderTest: ClusterMessenger

  @Before
  @Throws(Exception::class)
  fun setup() {
    MockitoAnnotations.initMocks(this)

    rpcAppendEntriesRequest = RpcAppendEntriesRequest.newBuilder().build()
    rpcAppendEntriesResponse = RpcAppendEntriesResponse.newBuilder().build()
    rpcRequestVoteRequest = RpcRequestVoteRequest.newBuilder().build()
    rpcRequestVoteResponse = RpcRequestVoteResponse.newBuilder().build()

    setupConverterRegistry()
    setupCluster()

    unitUnderTest = ClusterMessenger(clusterConnectionPool, converterRegistry)
  }

  @Throws(ExecutionException::class, InterruptedException::class)
  private fun setupCluster() {
    doReturn(channel)
        .`when`(clusterConnectionPool)
        .getChannel(node)

    /* Append Entries */

    doReturn(rpcAppendEntriesResponseFuture)
        .`when`(channel)
        .appendEntries(rpcAppendEntriesRequest)

    doReturn(rpcAppendEntriesResponse)
        .`when`(rpcAppendEntriesResponseFuture)
        .get(any(), any())

    doReturn(appendEntriesResponse)
        .`when`(converterRegistry)
        .convert<Any>(rpcAppendEntriesResponse)

    /* Request Vote */

    doReturn(rpcRequestVoteResponseFuture)
        .`when`(channel)
        .requestVote(rpcRequestVoteRequest)

    doReturn(rpcRequestVoteResponse)
        .`when`(rpcRequestVoteResponseFuture)
        .get(any(), any())

    doReturn(requestVoteResponse)
        .`when`(converterRegistry)
        .convert<Any>(rpcRequestVoteResponse)
  }

  private fun setupConverterRegistry() {
    doReturn(rpcAppendEntriesRequest)
        .`when`(converterRegistry)
        .convert<Any>(appendEntriesRequest)

    doReturn(rpcRequestVoteRequest)
        .`when`(converterRegistry)
        .convert<Any>(requestVoteRequest)
  }

  @Test
  fun `should send appendEntries with channel`() = runBlocking<Unit> {
    // Given / When
    val future = unitUnderTest.dispatch<AppendEntriesResponse>(node, appendEntriesRequest)

    // Then
    assertNotNull(future)
    verify(converterRegistry).convert<Any>(appendEntriesRequest)
    verify(channel).appendEntries(rpcAppendEntriesRequest)
  }

  @Test
  fun `should send requestVote with channel`() = runBlocking<Unit> {
    // Given / When
    val future = unitUnderTest.dispatch<RequestVoteResponse>(node, requestVoteRequest)

    // Then
    assertNotNull(future)
    verify(converterRegistry).convert<Any>(requestVoteRequest)
    verify(channel).requestVote(rpcRequestVoteRequest)
  }

  @Test(expected = NodeNotFoundException::class)
  fun `should throw exception for foreign node appendEntries`() = runBlocking<Unit> {
    // Given / When
    unitUnderTest.dispatch<Any>(mock(), appendEntriesRequest)

    // Then - exception thrown
  }

  @Test(expected = NodeNotFoundException::class)
  fun `should throw exception for foreign node requestVote`() = runBlocking<Unit> {
    // Given / When
    unitUnderTest.dispatch<Any>(mock(), requestVoteRequest)

    // Then - exception thrown
  }

  @Test
  @Throws(ExecutionException::class, InterruptedException::class)
  fun `should return appendEntriesResponse`() = runBlocking {
    // Given / When
    val appendEntriesResponse = unitUnderTest.dispatch<AppendEntriesResponse>(node, appendEntriesRequest)

    // Then
    assertSame(this@ClusterMessengerTest.appendEntriesResponse, appendEntriesResponse)
  }

  @Test
  @Throws(ExecutionException::class, InterruptedException::class)
  fun `should return requestVoteResponse`() = runBlocking {
    // Given / When
    val requestVoteResponse = unitUnderTest.dispatch<RequestVoteResponse>(node, requestVoteRequest)

    // Then
    assertSame(this@ClusterMessengerTest.requestVoteResponse, requestVoteResponse)
  }

  @Test
  fun `should call onFailure when channel fails`() = runBlocking<Unit> {
    // Given
    doThrow(ElkdRuntimeException())
        .`when`(channel)
        .appendEntries(rpcAppendEntriesRequest)

    // When
    var e: Exception? = null
    unitUnderTest.dispatch<AppendEntriesResponse>(node, appendEntriesRequest) {
      e = it
    }

    // Then
    assertNotNull(e)
  }
}
