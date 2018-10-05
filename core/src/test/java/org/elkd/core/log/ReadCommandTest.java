package org.elkd.core.log;

import org.elkd.core.log.LogCommandReasons.ReadReason;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ReadCommandTest {
  private static final ReadReason COMMAND_REASON = ReadReason.REPLICATION;
  private static final long READ_INDEX = 1;

  @Mock Log mReceiver;
  @Mock Entry mEntry;

  private ReadCommand mUnitUnderTest;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new ReadCommand(
        READ_INDEX,
        COMMAND_REASON,
        mReceiver
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

  @Test
  public void should_no_op_for_rollback() {
    // Given / When
    mUnitUnderTest.rollback();

    // Then
    verifyZeroInteractions(mReceiver);
  }
}
