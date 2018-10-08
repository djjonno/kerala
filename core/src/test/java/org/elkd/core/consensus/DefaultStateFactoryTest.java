package org.elkd.core.consensus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class DefaultStateFactoryTest {

  @Mock Consensus mConsensus;

  private DefaultStateFactory mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new DefaultStateFactory();
  }

  @Test
  public void should_create_singleton_instances() {
    // Given
    final State follower = mUnitUnderTest.getState(mConsensus, FollowerState.class);
    final State candidate = mUnitUnderTest.getState(mConsensus, CandidateState.class);
    final State leader = mUnitUnderTest.getState(mConsensus, LeaderState.class);

    // When
    final State otherFollower = mUnitUnderTest.getState(mConsensus, FollowerState.class);
    final State otherCandidate = mUnitUnderTest.getState(mConsensus, CandidateState.class);
    final State otherLeader = mUnitUnderTest.getState(mConsensus, LeaderState.class);

    // Then
    assertSame(follower, otherFollower);
    assertSame(candidate, otherCandidate);
    assertSame(leader, otherLeader);
  }

  @Test
  public void should_return_initial_state() {
    // Given
    final Class<? extends State> initialStateClass = DefaultStateFactory.INITIAL_STATE;

    // When
    final State initialState = mUnitUnderTest.getInitialState(mConsensus);

    // Then
    assertTrue(initialStateClass.isInstance(initialState));
  }
}
