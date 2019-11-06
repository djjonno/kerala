package org.kerala.core.log.commands

import org.kerala.core.log.LogChangeReason
import org.kerala.core.log.LogEntry
import org.kerala.core.log.ds.Log

class AppendCommand<E : LogEntry> constructor(
    private val entries: List<E>,
    override val reason: LogChangeReason
) : LogCommand<E> {

  override fun execute(log: Log<E>) {
    entries.forEach { log.append(it) }
  }

  companion object {
    @JvmStatic fun <E : LogEntry> build(entry: E, reason: LogChangeReason): AppendCommand<E> = build(listOf(entry), reason)
    @JvmStatic fun <E : LogEntry> build(entries: List<E>, reason: LogChangeReason): AppendCommand<E> = AppendCommand(entries, reason)
  }
}
