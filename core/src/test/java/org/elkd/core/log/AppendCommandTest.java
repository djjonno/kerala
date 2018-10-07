package org.elkd.core.log;

import com.google.common.collect.ImmutableList;
import org.elkd.core.log.LogCommandReasons.AppendReason;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AppendCommandTest {
  private static final AppendReason COMMAND_REASON = AppendReason.REPLICATION;

  @Mock Entry mEntry1;
  @Mock Entry mEntry2;
  @Mock Log<Entry> mReceiver;

  private AppendCommand mUnitUnderTest;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mUnitUnderTest = new AppendCommand(ImmutableList.of(
        mEntry1, mEntry2
    ), COMMAND_REASON, mReceiver);
  }

  @Test
  public void should_return_reason() {
    // Given / When - new AppendCommand

    // Then
    assertEquals(COMMAND_REASON, mUnitUnderTest.getReason());
  }

  @Test
  public void should_append_all_entries_to_receiver() {
    // Given / When
    mUnitUnderTest.execute();

    // Then
    verify(mReceiver).append(mEntry1);
    verify(mReceiver).append(mEntry2);
  }

  @Test
  public void should_return_index_of_last_appended_entry() {
    // Given
    final long first = 1;
    final long second = 1;
    doReturn(first)
        .when(mReceiver)
        .append(mEntry1);
    doReturn(second)
        .when(mReceiver)
        .append(mEntry2);

    // When
    final long index = mUnitUnderTest.execute();

    // Then
    assertEquals(second, index);
  }
}
