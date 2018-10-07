package org.elkd.core.log;

import org.elkd.core.log.LogCommandReasons.CommitReason;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class CommitCommandTest {
  private static final CommitReason COMMAND_REASON = CommitReason.REPLICATION;
  private static final long COMMIT_INDEX = 1;

  @Mock Log<Entry> mReceiver;

  private CommitCommand mUnitUnderTest;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new CommitCommand(
        COMMIT_INDEX,
        COMMAND_REASON,
        mReceiver
    );
  }

  @Test
  public void should_return_reason() {
    // Given / When - new CommitCommand

    // Then
    assertEquals(COMMAND_REASON, mUnitUnderTest.getReason());
  }

  @Test
  public void should_commit_index() {
    // Given / When
    mUnitUnderTest.execute();

    // Then
    verify(mReceiver).commit(COMMIT_INDEX);
  }
}
