package org.elkd.core.consensus;

import com.google.common.collect.ImmutableSet;
import org.elkd.core.log.Log;
import org.elkd.core.server.cluster.Node;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class LeaderContextTest {

  private static final long LAST_LOG_INDEX = 1;
  private static final long FOREIGN_INDEX = 10;

  @Mock Node mNode1;
  @Mock Node mNode2;
  @Mock Node mForeignNode;
  @Mock RaftContext mRaftContext;
  @Mock Log mLog;

  private LeaderContext mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new LeaderContext(ImmutableSet.of(mNode1, mNode2), LAST_LOG_INDEX);
  }

  @Test
  public void should_initialize_next_indices() {
    // Given / When - mUnitUnderTest

    // Then
    assertEquals(LAST_LOG_INDEX + 1, mUnitUnderTest.getNextIndex(mNode1));
    assertEquals(LAST_LOG_INDEX + 1, mUnitUnderTest.getNextIndex(mNode2));
  }

  @Test
  public void should_update_nextIndex() {
    // Given
    final long oldNextIndex = mUnitUnderTest.getNextIndex(mNode1);
    final long newNextIndex = oldNextIndex + 1;

    // When
    mUnitUnderTest.updateNextIndex(mNode1, newNextIndex);

    // Then
    assertEquals(newNextIndex, mUnitUnderTest.getNextIndex(mNode1));
  }

  @Test(expected = IllegalStateException.class)
  public void should_throw_exception_when_getting_nextIndex_with_foreign_node() {
    // Given / When
    mUnitUnderTest.getNextIndex(mForeignNode);

    // Then - exception thrown
  }

  @Test(expected = IllegalStateException.class)
  public void should_throw_exception_when_updating_nextIndex_with_foreign_node() {
    // Given / When
    mUnitUnderTest.updateNextIndex(mForeignNode, FOREIGN_INDEX);

    // Then - exception thrown
  }

  @Test
  public void should_initialize_match_indices() {
    // Given / When - mUnitUnderTest

    // Then
    assertEquals(LeaderContext.DEFAULT_MATCH_INDEX, mUnitUnderTest.getMatchIndex(mNode1));
    assertEquals(LeaderContext.DEFAULT_MATCH_INDEX, mUnitUnderTest.getMatchIndex(mNode2));
  }

  @Test
  public void should_update_matchIndex() {
    // Given
    final long oldMatchIndex = mUnitUnderTest.getMatchIndex(mNode1);
    final long newMatchIndex = oldMatchIndex + 1;

    // When
    mUnitUnderTest.updateMatchIndex(mNode1, newMatchIndex);

    // Then
    assertEquals(newMatchIndex, mUnitUnderTest.getMatchIndex(mNode1));
  }

  @Test(expected = IllegalStateException.class)
  public void should_throw_exception_when_getting_matchIndex_with_foreign_node() {
    // Given / When
    mUnitUnderTest.getMatchIndex(mForeignNode);

    // Then - exception thrown
  }

  @Test(expected = IllegalStateException.class)
  public void should_throw_exception_when_updating_matchIndex_with_foreign_node() {
    // Given / When
    mUnitUnderTest.updateMatchIndex(mForeignNode, FOREIGN_INDEX);

    // Then - exception thrown
  }
}
