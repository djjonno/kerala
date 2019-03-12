package org.elkd.core.log.commands

import org.elkd.core.log.Log
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.exceptions.NonSequentialAppendException

class AppendFromCommand<E> constructor(
    private val from: Long,
    private val entries: List<E>,
    override val reason: LogChangeReason) : LogCommand<E> {

  override fun execute(log: Log<E>) {
    if (log.lastIndex + 1 < from) {
      throw NonSequentialAppendException()
    }
    if (log.lastIndex >= from) {
      log.revert(from)
    }

    AppendCommand.build(entries, reason).execute(log)
  }

  companion object {
    fun <E> build(from: Long, entries: List<E>, reason: LogChangeReason): AppendFromCommand<E>
        = AppendFromCommand(from, entries, reason)
  }
}
