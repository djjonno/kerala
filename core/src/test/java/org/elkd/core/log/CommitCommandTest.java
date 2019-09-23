package org.elkd.core.log;

import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.commands.CommitCommand;
import org.elkd.core.log.ds.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class CommitCommandTest {
  private static final LogChangeReason COMMAND_REASON = LogChangeReason.REPLICATION;
  private static final long COMMIT_INDEX = 0;

  @Mock Log<Entry> mReceiver;

  private CommitCommand<Entry> mUnitUnderTest;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = CommitCommand.Companion.build(COMMIT_INDEX, COMMAND_REASON);
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
    mUnitUnderTest.execute(mReceiver);

    // Then
    verify(mReceiver).commit(COMMIT_INDEX);
  }
}
