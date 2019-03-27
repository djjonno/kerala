package org.elkd.core.raft;

import org.elkd.core.config.ConfigProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

public class DefaultRaftDelegateFactoryTest {

  @Mock Raft mRaft;

  private DefaultDelegateFactory mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new DefaultDelegateFactory();

    doReturn(ConfigProvider.compileConfig(new String[] { }))
        .when(mRaft)
        .getConfig();
  }

  @Test
  public void should_create_singleton_instances() {
    // Given
    final State follower = mUnitUnderTest.getDelegate(mRaft, RaftFollowerDelegate.class);
    final State candidate = mUnitUnderTest.getDelegate(mRaft, RaftCandidateDelegate.class);
    final State leader = mUnitUnderTest.getDelegate(mRaft, RaftLeaderDelegate.class);

    // When
    final State otherFollower = mUnitUnderTest.getDelegate(mRaft, RaftFollowerDelegate.class);
    final State otherCandidate = mUnitUnderTest.getDelegate(mRaft, RaftCandidateDelegate.class);
    final State otherLeader = mUnitUnderTest.getDelegate(mRaft, RaftLeaderDelegate.class);

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
    final State initialDelegate = mUnitUnderTest.getInitialDelegate(mRaft);

    // Then
    assertTrue(initialStateClass.isInstance(initialDelegate));
  }
}
