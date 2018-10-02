package org.elkd.core.log;

import com.google.common.collect.ImmutableList;
import org.elkd.core.log.LogChangeReasons.AppendReason;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.elkd.core.log.LogChangeReasons.AppendReason.REPLICATION;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AppendEntriesCommandTest {
  private static final String ENTRY_1_TYPE = "entry1";
  private static final String ENTRY_2_TYPE = "entry2";
  private static final Entry ENTRY_1 = Entry.builder(ENTRY_1_TYPE).build();
  private static final Entry ENTRY_2 = Entry.builder(ENTRY_2_TYPE).build();

  @Test
  public void should_contain_all_entries() {
    // Given / When
    final LogCommand command = AppendEntriesCommand.build(
        REPLICATION,
        ImmutableList.of(ENTRY_1, ENTRY_2)
    );

    // Then
    final List<Entry> entries = command.getOperations().stream()
        .map(logOperation -> (AppendOperation)logOperation)
        .map(AppendOperation::getEntry).collect(Collectors.toList());
    assertThat(entries, hasItems(ENTRY_1, ENTRY_2));
  }

  @Test
  public void should_have_log_command_name() {
    // Given / When
    final AppendReason reason = REPLICATION;
    final LogCommand command = AppendEntriesCommand.build(
        reason,
        ImmutableList.of(ENTRY_1, ENTRY_2)
    );

    // Then
    assertEquals(LogCommands.APPEND_ENTRIES, command.getCommandName());
    assertEquals(reason, command.getReason());
  }
}
