package org.elkd.core.consensus;

import org.elkd.core.cluster.ClusterConnectionPool;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DefaultRaftDelegateFactoryTest {

  @Mock Raft mRaft;

  private DefaultStateFactory mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new DefaultStateFactory();
    doReturn(mock(ClusterConnectionPool.class))
        .when(mRaft)
        .getClusterConnectionPool();
  }

  @Test
  public void should_create_singleton_instances() {
    // Given
    final State follower = mUnitUnderTest.getDelegate(mRaft, RaftFollowerState.class);
    final State candidate = mUnitUnderTest.getDelegate(mRaft, RaftCandidateState.class);
    final State leader = mUnitUnderTest.getDelegate(mRaft, RaftLeaderState.class);

    // When
    final State otherFollower = mUnitUnderTest.getDelegate(mRaft, RaftFollowerState.class);
    final State otherCandidate = mUnitUnderTest.getDelegate(mRaft, RaftCandidateState.class);
    final State otherLeader = mUnitUnderTest.getDelegate(mRaft, RaftLeaderState.class);

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
    final State initialDelegate = mUnitUnderTest.getInitialDelegate(mRaft);

    // Then
    assertTrue(initialStateClass.isInstance(initialDelegate));
  }
}
