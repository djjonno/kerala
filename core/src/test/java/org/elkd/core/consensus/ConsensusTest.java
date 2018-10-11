package org.elkd.core.consensus;

import org.elkd.core.cluster.ClusterConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class ConsensusTest {

  @Mock State mState1;
  @Mock State mState2;
  @Mock ClusterConfig mClusterConfig;
  @Mock ConsensusContext mConsensusContext;
  @Mock AbstractStateFactory mStateFactory;
  @Mock AppendEntriesRequest mAppendEntriesRequest;
  @Mock AppendEntriesResponse mAppendEntriesResponse;
  @Mock RequestVotesRequest mRequestVotesRequest;
  @Mock RequestVotesResponse mRequestVotesResponse;

  private Consensus mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new Consensus(
        mClusterConfig,
        mConsensusContext,
        mStateFactory
    );

    setupCommonExpectations();
  }

  private void setupCommonExpectations() {
    doReturn(mState1)
        .when(mStateFactory)
        .getInitialState(any());
    doReturn(mState2)
        .when(mStateFactory)
        .getState(any(), any());

    doReturn(mAppendEntriesResponse)
        .when(mState1)
        .handleAppendEntries(mAppendEntriesRequest);
    doReturn(mRequestVotesResponse)
        .when(mState1)
        .handleRequestVotes(mRequestVotesRequest);
  }

  @Test
  public void should_get_initial_state_and_activate_on_initialize() {
    // Given / When
    mUnitUnderTest.initialize();

    // Then
    verify(mStateFactory).getInitialState(mUnitUnderTest);
    verify(mState1).on();
  }

  @Test
  public void should_transition_state_on_off_on_transition() {
    // Given
    mUnitUnderTest.initialize();
    final Class<? extends State> state = LeaderState.class;

    // When
    mUnitUnderTest.transition(state);

    // Then
    verify(mState1).off();
    verify(mStateFactory).getState(mUnitUnderTest, state);
    verify(mState2).on();
  }

  @Test
  public void should_route_appendEntries_to_active_state() {
    // Given
    mUnitUnderTest.initialize();

    // When
    final AppendEntriesResponse response = mUnitUnderTest.routeAppendEntries(mAppendEntriesRequest);

    // Then
    verify(mState1).handleAppendEntries(mAppendEntriesRequest);
    assertEquals(mAppendEntriesResponse, response);
  }

  @Test
  public void should_route_requestVotes_to_active_state() {
    // Given
    mUnitUnderTest.initialize();

    // When
    final RequestVotesResponse response = mUnitUnderTest.routeRequestVotes(mRequestVotesRequest);

    // Then
    verify(mState1).handleRequestVotes(mRequestVotesRequest);
    assertEquals(mRequestVotesResponse, response);
  }

  @Test
  public void should_get_context() {
    // Given / When - mUnitUnderTest

    // Then
    assertEquals(mConsensusContext, mUnitUnderTest.getContext());
  }

  @Test
  public void should_get_cluster_config() {
    // Given / When - mUnitUnderTest

    // Then
    assertEquals(mClusterConfig, mUnitUnderTest.getClusterConfig());
  }
}
