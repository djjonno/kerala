package org.elkd.core.consensus;

import io.grpc.stub.StreamObserver;
import org.elkd.core.cluster.ClusterSet;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.log.LogInvoker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class RaftTest {
  @Mock RaftState mRaft1;
  @Mock RaftState mRaft2;
  @Mock NodeState mNodeState;
  @Mock ClusterSet mClusterSet;
  @Mock LogInvoker<Entry> mLogInvoker;
  @Mock AbstractStateFactory mStateFactory;
  @Mock RequestVoteRequest mRequestVoteRequest;
  @Mock AppendEntriesRequest mAppendEntriesRequest;
  @Mock StreamObserver<RequestVoteResponse> mRequestVotesResponseObserver;
  @Mock StreamObserver<AppendEntriesResponse> mAppendEntriesResponseObserver;

  private Raft mUnitUnderTest;
  private ExecutorService mExecutorService;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mExecutorService = Executors.newSingleThreadExecutor();

    mUnitUnderTest = new Raft(
        mLogInvoker,
        mClusterSet,
        mNodeState,
        mStateFactory,
        mExecutorService
    );

    setupCommonExpectations();
  }

  private void setupCommonExpectations() {
    doReturn(mRaft1)
        .when(mStateFactory)
        .getInitialDelegate(any());
    doReturn(mRaft2)
        .when(mStateFactory)
        .getDelegate(any(), any());
  }

  @Test
  public void should_get_initial_state_and_activate_on_initialize() {
    // Given / When
    mUnitUnderTest.initialize();

    // Then
    verify(mStateFactory).getInitialDelegate(mUnitUnderTest);
    verify(mRaft1).on();
  }

  @Test
  public void should_transition_state_for_transition() throws InterruptedException {
    // Given
    mUnitUnderTest.initialize();
    final Class<? extends RaftState> state = RaftLeaderState.class;

    // When
    mUnitUnderTest.transition(state);

    // Then
    mExecutorService.awaitTermination(1, TimeUnit.SECONDS); // TODO: find a better way
    verify(mRaft1).off();
    verify(mStateFactory).getDelegate(mUnitUnderTest, state);
    verify(mRaft2).on();
  }

  @Test
  public void should_route_appendEntries_to_active_state() {
    // Given
    mUnitUnderTest.initialize();

    // When
    mUnitUnderTest.delegateAppendEntries(mAppendEntriesRequest, mAppendEntriesResponseObserver);

    // Then
    verify(mRaft1).delegateAppendEntries(mAppendEntriesRequest, mAppendEntriesResponseObserver);
  }

  @Test
  public void should_route_requestVotes_to_active_state() {
    // Given
    mUnitUnderTest.initialize();

    // When
    mUnitUnderTest.delegateRequestVote(mRequestVoteRequest, mRequestVotesResponseObserver);

    // Then
    verify(mRaft1).delegateRequestVote(mRequestVoteRequest, mRequestVotesResponseObserver);
  }

  @Test
  public void should_get_context() {
    // Given / When - mUnitUnderTest

    // Then
    assertEquals(mNodeState, mUnitUnderTest.getNodeState());
  }

  @Test
  public void should_get_clusterSet() {
    // Given / When - mUnitUnderTest

    // Then
    assertEquals(mClusterSet, mUnitUnderTest.getClusterSet());
  }
}
