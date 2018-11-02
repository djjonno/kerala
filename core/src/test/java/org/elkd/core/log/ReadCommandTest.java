package org.elkd.core.log;

import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.commands.ReadCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class ReadCommandTest {
  private static final LogChangeReason COMMAND_REASON = LogChangeReason.REPLICATION;
  private static final long READ_INDEX = 1;

  @Mock Log<Entry> mReceiver;
  @Mock Entry mEntry;

  private ReadCommand mUnitUnderTest;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new ReadCommand(
        READ_INDEX,
        mReceiver, COMMAND_REASON
    );
  }

  @Test
  public void should_return_reason() {
    // Given / When - new ReadCommand

    // Then
    assertEquals(COMMAND_REASON, mUnitUnderTest.getReason());
  }

  @Test
  public void should_execute_read_command_on_receiver() {
    // Given / When
    mUnitUnderTest.execute();

    // Then
    verify(mReceiver).read(READ_INDEX);
  }

  @Test
  public void should_return_entry_for_read_index() {
    // Given
    doReturn(mEntry)
        .when(mReceiver)
        .read(READ_INDEX);

    // When
    final Entry entry = mUnitUnderTest.execute();

    // Then
    assertEquals(mEntry, entry);
  }
}
