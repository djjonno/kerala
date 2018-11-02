package org.elkd.core.consensus;

import io.grpc.stub.StreamObserver;
import org.elkd.core.cluster.ClusterConfig;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVotesRequest;
import org.elkd.core.consensus.messages.RequestVotesResponse;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.LogInvoker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class RaftTest {

  @Mock LogInvoker<Entry> mLogInvoker;
  @Mock RaftState mRaft1;
  @Mock RaftState mRaft2;
  @Mock ClusterConfig mClusterConfig;
  @Mock NodeState mNodeState;
  @Mock AbstractStateFactory mStateFactory;
  @Mock AppendEntriesRequest mAppendEntriesRequest;
  @Mock RequestVotesRequest mRequestVotesRequest;
  @Mock StreamObserver<AppendEntriesResponse> mAppendEntriesResponseObserver;
  @Mock StreamObserver<RequestVotesResponse> mRequestVotesResponseObserver;

  private Raft mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new Raft(
        mLogInvoker,
        mClusterConfig,
        mNodeState,
        mStateFactory
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
  public void should_transition_state_on_off_on_transition() {
    // Given
    mUnitUnderTest.initialize();
    final Class<? extends RaftState> state = RaftLeaderState.class;

    // When
    mUnitUnderTest.transition(state);

    // Then
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
    mUnitUnderTest.delegateRequestVotes(mRequestVotesRequest, mRequestVotesResponseObserver);

    // Then
    verify(mRaft1).delegateRequestVotes(mRequestVotesRequest, mRequestVotesResponseObserver);
  }

  @Test
  public void should_get_context() {
    // Given / When - mUnitUnderTest

    // Then
    assertEquals(mNodeState, mUnitUnderTest.getNodeState());
  }

  @Test
  public void should_get_cluster_config() {
    // Given / When - mUnitUnderTest

    // Then
    assertEquals(mClusterConfig, mUnitUnderTest.getClusterConfig());
  }
}
