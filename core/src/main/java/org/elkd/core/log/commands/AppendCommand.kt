package org.elkd.core.log.commands

import org.elkd.core.log.Log
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.LogEntry

class AppendCommand<E : LogEntry> constructor(
    private val entries: List<E>,
    override val reason: LogChangeReason) : LogCommand<E> {

  override fun execute(log: Log<E>) {
    entries.forEach { log.append(it) }
  }

  companion object {
    @JvmStatic fun <E : LogEntry> build(entry: E, reason: LogChangeReason): AppendCommand<E> = build(listOf(entry), reason)
    @JvmStatic fun <E : LogEntry> build(entries: List<E>, reason: LogChangeReason): AppendCommand<E> = AppendCommand(entries, reason)
  }
}
