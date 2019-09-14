package org.elkd.core.log.commands

import org.elkd.core.log.Log
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.LogEntry
import org.elkd.core.log.exceptions.NonSequentialAppendException

/**
 * Append from the given index.
 *
 * @param from append at this logger index and onwards. Will revert current index if not yet committed.
 * @param entries List<E> to append
 * @param reason instigator of logger change
 *
 * @see LogChangeReason
 */
class AppendFromCommand<E : LogEntry> constructor(
    private val from: Long,
    private val entries: List<E>,
    override val reason: LogChangeReason) : LogCommand<E> {

  override fun execute(log: Log<E>) {
    if (log.lastIndex + 1 < from) {
      throw NonSequentialAppendException(log.lastIndex, from)
    }
    if (from <= log.lastIndex) {
      log.revert(from)
    }

    AppendCommand.build(entries, reason).execute(log)
  }

  companion object {
    @JvmStatic fun <E : LogEntry> build(from: Long, entries: List<E>, reason: LogChangeReason): AppendFromCommand<E>
        = AppendFromCommand(from, entries, reason)
  }
}
