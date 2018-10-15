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

  @Mock Delegate mDelegate1;
  @Mock Delegate mDelegate2;
  @Mock ClusterConfig mClusterConfig;
  @Mock
  NodeState mNodeState;
  @Mock AbstractDelegateFactory mStateFactory;
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
        mNodeState,
        mStateFactory
    );

    setupCommonExpectations();
  }

  private void setupCommonExpectations() {
    doReturn(mDelegate1)
        .when(mStateFactory)
        .getInitialDelegate(any());
    doReturn(mDelegate2)
        .when(mStateFactory)
        .getDelegate(any(), any());

    doReturn(mAppendEntriesResponse)
        .when(mDelegate1)
        .delegateAppendEntries(mAppendEntriesRequest);
    doReturn(mRequestVotesResponse)
        .when(mDelegate1)
        .delegateRequestVotes(mRequestVotesRequest);
  }

  @Test
  public void should_get_initial_state_and_activate_on_initialize() {
    // Given / When
    mUnitUnderTest.initialize();

    // Then
    verify(mStateFactory).getInitialDelegate(mUnitUnderTest);
    verify(mDelegate1).on();
  }

  @Test
  public void should_transition_state_on_off_on_transition() {
    // Given
    mUnitUnderTest.initialize();
    final Class<? extends Delegate> delegate = LeaderDelegate.class;

    // When
    mUnitUnderTest.transition(delegate);

    // Then
    verify(mDelegate1).off();
    verify(mStateFactory).getDelegate(mUnitUnderTest, delegate);
    verify(mDelegate2).on();
  }

  @Test
  public void should_route_appendEntries_to_active_state() {
    // Given
    mUnitUnderTest.initialize();

    // When
    final AppendEntriesResponse response = mUnitUnderTest.delegateAppendEntries(mAppendEntriesRequest);

    // Then
    verify(mDelegate1).delegateAppendEntries(mAppendEntriesRequest);
    assertEquals(mAppendEntriesResponse, response);
  }

  @Test
  public void should_route_requestVotes_to_active_state() {
    // Given
    mUnitUnderTest.initialize();

    // When
    final RequestVotesResponse response = mUnitUnderTest.delegateRequestVotes(mRequestVotesRequest);

    // Then
    verify(mDelegate1).delegateRequestVotes(mRequestVotesRequest);
    assertEquals(mRequestVotesResponse, response);
  }

  @Test
  public void should_get_context() {
    // Given / When - mUnitUnderTest

    // Then
    assertEquals(mNodeState, mUnitUnderTest.getContext());
  }

  @Test
  public void should_get_cluster_config() {
    // Given / When - mUnitUnderTest

    // Then
    assertEquals(mClusterConfig, mUnitUnderTest.getClusterConfig());
  }
}
