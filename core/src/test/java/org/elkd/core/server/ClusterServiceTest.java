package org.elkd.core.server;

import io.grpc.stub.StreamObserver;
import org.elkd.core.consensus.RaftDelegate;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.server.converters.ConverterRegistry;
import org.elkd.core.server.converters.ResponseConverterStreamDecorator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

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
  public void should_delegate_appendEntries_to_delegate() {
    // Given
    final RpcAppendEntriesRequest request = RpcAppendEntriesRequest.newBuilder().build();
    final RpcAppendEntriesResponse response = RpcAppendEntriesResponse.newBuilder().build();
    doReturn(mAppendEntriesRequest)
        .when(mConverterRegistry)
        .convert(request);
    doReturn(response)
        .when(mConverterRegistry)
        .convert(mAppendEntriesResponse);

    // When
    mUnitUnderTest.appendEntries(request, mRpcAppendEntriesStreamObserver);

    // Then
    final ArgumentCaptor<ResponseConverterStreamDecorator<AppendEntriesResponse, RpcAppendEntriesResponse>> captor =
        ArgumentCaptor.forClass(ResponseConverterStreamDecorator.class);
    verify(mConverterRegistry).convert(request);
    verify(mRaftDelegate).delegateAppendEntries(eq(mAppendEntriesRequest), captor.capture());
    captor.getValue().onNext(mAppendEntriesResponse);
    verify(mRpcAppendEntriesStreamObserver).onNext(response);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void should_delegate_requestVote_to_delegate() {
    // Given
    final RpcRequestVoteRequest request = RpcRequestVoteRequest.newBuilder().build();
    final RpcRequestVoteResponse response = RpcRequestVoteResponse.newBuilder().build();
    doReturn(mRequestVoteRequest)
        .when(mConverterRegistry)
        .convert(request);
    doReturn(response)
        .when(mConverterRegistry)
        .convert(mRequestVoteResponse);

    // When
    mUnitUnderTest.requestVote(request, mRpcRequestVoteStreamObserver);

    // Then
    final ArgumentCaptor<ResponseConverterStreamDecorator<RequestVoteResponse, RpcRequestVoteResponse>> captor =
        ArgumentCaptor.forClass(ResponseConverterStreamDecorator.class);
    verify(mConverterRegistry).convert(request);
    verify(mRaftDelegate).delegateRequestVote(eq(mRequestVoteRequest), captor.capture());
    captor.getValue().onNext(mRequestVoteResponse);
    verify(mRpcRequestVoteStreamObserver).onNext(response);
  }
}
