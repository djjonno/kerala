package org.elkd.core.server.cluster;

import io.grpc.stub.StreamObserver;
import org.elkd.core.raft.RaftDelegate;
import org.elkd.core.raft.messages.AppendEntriesRequest;
import org.elkd.core.raft.messages.AppendEntriesResponse;
import org.elkd.core.raft.messages.RequestVoteRequest;
import org.elkd.core.raft.messages.RequestVoteResponse;
import org.elkd.core.server.RpcAppendEntriesRequest;
import org.elkd.core.server.RpcAppendEntriesResponse;
import org.elkd.core.server.RpcRequestVoteRequest;
import org.elkd.core.server.RpcRequestVoteResponse;
import org.elkd.core.server.converters.ConverterRegistry;
import org.elkd.core.server.converters.StreamConverterDecorator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ClusterServiceTest {
  @Mock RaftDelegate mRaftDelegate;
  @Mock ConverterRegistry mConverterRegistry;

  @Mock AppendEntriesRequest mAppendEntriesRequest;
  @Mock RequestVoteRequest mRequestVoteRequest;

  @Mock AppendEntriesResponse mAppendEntriesResponse;
  @Mock RequestVoteResponse mRequestVoteResponse;

  @Mock StreamObserver<RpcAppendEntriesResponse> mRpcAppendEntriesStreamObserver;
  @Mock StreamObserver<RpcRequestVoteResponse> mRpcRequestVoteStreamObserver;

  private ClusterService mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    mUnitUnderTest = new ClusterService(mRaftDelegate, mConverterRegistry);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void should_delegate_converted_appendEntries_to_delegate() throws InterruptedException {
    // Given
    final RpcAppendEntriesRequest request = RpcAppendEntriesRequest.newBuilder().build();
    setupConverterRegistry(request, mAppendEntriesRequest);

    // When
    mUnitUnderTest.appendEntries(request, mRpcAppendEntriesStreamObserver);

    // Then
    verify(mConverterRegistry).convert(request);
    verify(mRaftDelegate).delegateAppendEntries(eq(mAppendEntriesRequest), any(StreamConverterDecorator.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void should_pass_decorated_stream_observer_on_appendEntries() {
    // Given
    final RpcAppendEntriesRequest request = RpcAppendEntriesRequest.newBuilder().build();
    final RpcAppendEntriesResponse response = RpcAppendEntriesResponse.newBuilder().build();
    setupConverterRegistry(request, mAppendEntriesRequest);
    setupConverterRegistry(mAppendEntriesResponse, response);

    // When
    mUnitUnderTest.appendEntries(request, mRpcAppendEntriesStreamObserver);

    // Then
    final ArgumentCaptor<StreamConverterDecorator<AppendEntriesResponse, RpcAppendEntriesResponse>> captor =
        ArgumentCaptor.forClass(StreamConverterDecorator.class);
    verify(mRaftDelegate).delegateAppendEntries(eq(mAppendEntriesRequest), captor.capture());

    // Then
    final StreamConverterDecorator<AppendEntriesResponse, RpcAppendEntriesResponse> streamObserver = captor.getValue();
    streamObserver.onNext(mAppendEntriesResponse);
    verify(mRpcAppendEntriesStreamObserver).onNext(response);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void should_delegate_requestVote_to_delegate() throws InterruptedException {
    // Given
    final RpcRequestVoteRequest request = RpcRequestVoteRequest.newBuilder().build();
    setupConverterRegistry(request, mRequestVoteRequest);

    // When
    mUnitUnderTest.requestVote(request, mRpcRequestVoteStreamObserver);

    // Then
    verify(mConverterRegistry).convert(request);
    verify(mRaftDelegate).delegateRequestVote(eq(mRequestVoteRequest), any(StreamConverterDecorator.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void should_pass_decorated_stream_observer_on_requestVote() {
    // Given
    final RpcRequestVoteRequest request = RpcRequestVoteRequest.newBuilder().build();
    final RpcRequestVoteResponse response = RpcRequestVoteResponse.newBuilder().build();
    setupConverterRegistry(request, mRequestVoteRequest);
    setupConverterRegistry(mRequestVoteResponse, response);

    // When
    mUnitUnderTest.requestVote(request, mRpcRequestVoteStreamObserver);

    // Then
    final ArgumentCaptor<StreamConverterDecorator<RequestVoteResponse, RpcRequestVoteResponse>> captor =
        ArgumentCaptor.forClass(StreamConverterDecorator.class);
    verify(mRaftDelegate).delegateRequestVote(eq(mRequestVoteRequest), captor.capture());

    // Then
    final StreamConverterDecorator<RequestVoteResponse, RpcRequestVoteResponse> streamObserver = captor.getValue();
    streamObserver.onNext(mRequestVoteResponse);
    verify(mRpcRequestVoteStreamObserver).onNext(response);
  }

  @Test
  public void should_call_onError_given_appendEntries_delegation_throws_exception() {
    // Given
    final RpcAppendEntriesRequest request = RpcAppendEntriesRequest.newBuilder().build();
    final RuntimeException exception = new RuntimeException();
    doThrow(exception)
        .when(mRaftDelegate)
        .delegateAppendEntries(any(), any());

    // When
    mUnitUnderTest.appendEntries(request, mRpcAppendEntriesStreamObserver);

    // Then
    verify(mRpcAppendEntriesStreamObserver).onError(exception);
    verify(mRpcAppendEntriesStreamObserver).onCompleted();
  }

  @Test
  public void should_call_onError_given_requestVote_delegation_throws_exception() {
    // Given
    final RpcRequestVoteRequest request = RpcRequestVoteRequest.newBuilder().build();
    final RuntimeException exception = new RuntimeException();
    doThrow(exception)
        .when(mRaftDelegate)
        .delegateRequestVote(any(), any());

    // When
    mUnitUnderTest.requestVote(request, mRpcRequestVoteStreamObserver);

    // Then
    verify(mRpcRequestVoteStreamObserver).onError(exception);
    verify(mRpcRequestVoteStreamObserver).onCompleted();
  }

  private void setupConverterRegistry(final Object from, final Object to) {
    doReturn(to)
        .when(mConverterRegistry)
        .convert(from);
  }
}
