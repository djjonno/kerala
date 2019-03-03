package org.elkd.core.log;

import com.google.common.collect.ImmutableList;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.commands.AppendCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AppendCommandTest {
  private static final LogChangeReason COMMAND_REASON = LogChangeReason.REPLICATION;

  @Mock Entry mEntry1;
  @Mock Entry mEntry2;
  @Mock Log<Entry> mReceiver;

  private AppendCommand mUnitUnderTest;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mUnitUnderTest = AppendCommand.Companion.build(ImmutableList.of(mEntry1, mEntry2), COMMAND_REASON);
  }

  @Test
  public void should_return_reason() {
    // Given / When - new AppendCommand

    // Then
    assertEquals(COMMAND_REASON, mUnitUnderTest.getReason());
  }

  @Test
  public void should_append_single_entry() {
    // Given
    final AppendCommand command = AppendCommand.Companion.build(ImmutableList.of(mEntry1, mEntry2), COMMAND_REASON);

    // When
    command.execute(mReceiver);

    // Then
    verify(mReceiver, times(1)).append(mEntry1);
  }

  @Test
  public void should_append_all_entries_to_receiver() {
    // Given / When
    mUnitUnderTest.execute(mReceiver);

    // Then
    verify(mReceiver).append(mEntry1);
    verify(mReceiver).append(mEntry2);
  }
}
