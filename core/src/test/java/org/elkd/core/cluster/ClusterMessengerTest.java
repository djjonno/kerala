package org.elkd.core.cluster;

import com.google.common.util.concurrent.ListenableFuture;
import org.elkd.core.cluster.exceptions.NodeNotFoundException;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.server.RpcAppendEntriesRequest;
import org.elkd.core.server.RpcAppendEntriesResponse;
import org.elkd.core.server.RpcRequestVoteRequest;
import org.elkd.core.server.RpcRequestVoteResponse;
import org.elkd.core.server.converters.ConverterRegistry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ClusterMessengerTest {
  @Mock Node mNode;
  @Mock ClusterConnectionPool.Channel mChannel;
  @Mock ClusterConnectionPool mClusterConnectionPool;
  @Mock ConverterRegistry mConverterRegistry;

  @Mock AppendEntriesRequest mAppendEntriesRequest;
  @Mock AppendEntriesResponse mAppendEntriesResponse;
  @Mock ListenableFuture<RpcAppendEntriesResponse> mRpcAppendEntriesResponseFuture;
  RpcAppendEntriesRequest mRpcAppendEntriesRequest;
  RpcAppendEntriesResponse mRpcAppendEntriesResponse;

  @Mock RequestVoteRequest mRequestVoteRequest;
  @Mock RequestVoteResponse mRequestVoteResponse;
  @Mock ListenableFuture<RpcRequestVoteRequest> mRpcRequestVoteResponseFuture;
  RpcRequestVoteRequest mRpcRequestVoteRequest;
  RpcRequestVoteResponse mRpcRequestVoteResponse;

  private ClusterMessenger mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mRpcAppendEntriesRequest = RpcAppendEntriesRequest.newBuilder().build();
    mRpcAppendEntriesResponse = RpcAppendEntriesResponse.newBuilder().build();
    mRpcRequestVoteRequest = RpcRequestVoteRequest.newBuilder().build();
    mRpcRequestVoteResponse = RpcRequestVoteResponse.newBuilder().build();

    setupConverterRegistry();
    setupCluster();

    mUnitUnderTest = new ClusterMessenger(mClusterConnectionPool, mConverterRegistry);
  }

  private void setupCluster() throws ExecutionException, InterruptedException {
    doReturn(mChannel)
        .when(mClusterConnectionPool)
        .getChannel(mNode);

    /* Append Entries */

    doReturn(mRpcAppendEntriesResponseFuture)
        .when(mChannel)
        .appendEntries(mRpcAppendEntriesRequest);

    doReturn(mRpcAppendEntriesResponse)
        .when(mRpcAppendEntriesResponseFuture)
        .get();

    doReturn(mAppendEntriesResponse)
        .when(mConverterRegistry)
        .convert(mRpcAppendEntriesResponse);

    /* Request Vote */

    doReturn(mRpcRequestVoteResponseFuture)
        .when(mChannel)
        .requestVote(mRpcRequestVoteRequest);

    doReturn(mRpcRequestVoteResponse)
        .when(mRpcRequestVoteResponseFuture)
        .get();

    doReturn(mRequestVoteResponse)
        .when(mConverterRegistry)
        .convert(mRpcRequestVoteResponse);
  }

  private void setupConverterRegistry() {
    doReturn(mRpcAppendEntriesRequest)
        .when(mConverterRegistry)
        .convert(mAppendEntriesRequest);

    doReturn(mRpcRequestVoteRequest)
        .when(mConverterRegistry)
        .convert(mRequestVoteRequest);
  }

  @Test
  public void should_send_appendEntries_with_channel() {
    // Given / When
    final ListenableFuture<AppendEntriesResponse> future = mUnitUnderTest.appendEntries(mNode, mAppendEntriesRequest);

    // Then
    assertNotNull(future);
    verify(mConverterRegistry).convert(mAppendEntriesRequest);
    verify(mChannel).appendEntries(mRpcAppendEntriesRequest);
  }

  @Test
  public void should_send_requestVote_with_channel() {
    // Given / When
    final ListenableFuture<RequestVoteResponse> future = mUnitUnderTest.requestVote(mNode, mRequestVoteRequest);

    // Then
    assertNotNull(future);
    verify(mConverterRegistry).convert(mRequestVoteRequest);
    verify(mChannel).requestVote(mRpcRequestVoteRequest);
  }

  @Test(expected = NodeNotFoundException.class)
  public void should_throw_exception_for_foreign_node_appendEntries() {
    // Given / When
    mUnitUnderTest.appendEntries(mock(Node.class), mAppendEntriesRequest);

    // Then - exception thrown
  }

  @Test(expected = NodeNotFoundException.class)
  public void should_throw_exception_for_foreign_node_requestVote() {
    // Given / When
    mUnitUnderTest.requestVote(mock(Node.class), mRequestVoteRequest);

    // Then - exception thrown
  }

  @Test
  public void should_return_appendEntries_listenableFutureDecorator() throws ExecutionException, InterruptedException {
    // Given
    final Runnable runnable = mock(Runnable.class);
    final ExecutorService executor = mock(ExecutorService.class);
    final ListenableFuture<AppendEntriesResponse> future = mUnitUnderTest.appendEntries(mNode, mAppendEntriesRequest);

    // When
    future.addListener(runnable, executor);
    final AppendEntriesResponse appendEntriesResponse = future.get();

    // Then
    assertSame(mAppendEntriesResponse, appendEntriesResponse);
    verify(mRpcAppendEntriesResponseFuture).addListener(runnable, executor);
  }

  @Test
  public void shold_return_requestVote_listenableFutureDecorator() throws ExecutionException, InterruptedException {
    // Given
    final Runnable runnable = mock(Runnable.class);
    final ExecutorService executor = mock(ExecutorService.class);
    final ListenableFuture<RequestVoteResponse> future = mUnitUnderTest.requestVote(mNode, mRequestVoteRequest);

    // When
    future.addListener(runnable, executor);
    final RequestVoteResponse requestVoteResponse = future.get();

    // Then
    assertSame(mRequestVoteResponse, requestVoteResponse);
    verify(mRpcRequestVoteResponseFuture).addListener(runnable, executor);
  }
}
