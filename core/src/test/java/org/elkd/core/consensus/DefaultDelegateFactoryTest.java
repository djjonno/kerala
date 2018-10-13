package org.elkd.core.consensus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class DefaultDelegateFactoryTest {

  @Mock Consensus mConsensus;

  private DefaultDelegateFactory mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new DefaultDelegateFactory();
  }

  @Test
  public void should_create_singleton_instances() {
    // Given
    final State follower = mUnitUnderTest.getDelegate(mConsensus, FollowerDelegate.class);
    final State candidate = mUnitUnderTest.getDelegate(mConsensus, CandidateDelegate.class);
    final State leader = mUnitUnderTest.getDelegate(mConsensus, LeaderDelegate.class);

    // When
    final State otherFollower = mUnitUnderTest.getDelegate(mConsensus, FollowerDelegate.class);
    final State otherCandidate = mUnitUnderTest.getDelegate(mConsensus, CandidateDelegate.class);
    final State otherLeader = mUnitUnderTest.getDelegate(mConsensus, LeaderDelegate.class);

    // Then
    assertSame(follower, otherFollower);
    assertSame(candidate, otherCandidate);
    assertSame(leader, otherLeader);
  }

  @Test
  public void should_return_initial_state() {
    // Given
    final Class<? extends State> initialStateClass = DefaultDelegateFactory.INITIAL_STATE;

    // When
    final State initialDelegate = mUnitUnderTest.getInitialDelegate(mConsensus);

    // Then
    assertTrue(initialStateClass.isInstance(initialDelegate));
  }
}
