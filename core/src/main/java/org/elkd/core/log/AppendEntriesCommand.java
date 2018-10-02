package org.elkd.core.log;

import org.elkd.core.log.LogChangeReasons.AppendReason;

import java.util.List;

public class AppendEntriesCommand {
  private AppendEntriesCommand() { }

  public static LogCommand build(final AppendReason reason, final List<Entry> entries) {
    final LogCommandBuilder commandBuilder = LogCommandBuilder.builder(
        LogCommands.APPEND_ENTRIES,
        reason
    );

    entries.forEach(commandBuilder::withAppendOperation);

    return commandBuilder.build();
  }
}
