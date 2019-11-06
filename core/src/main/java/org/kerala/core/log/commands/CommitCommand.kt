package org.kerala.core.log.commands

import org.kerala.core.log.LogChangeReason
import org.kerala.core.log.LogEntry
import org.kerala.core.log.ds.Log

class CommitCommand<E : LogEntry> constructor(
    private val index: Long,
    override val reason: LogChangeReason
) : LogCommand<E> {

  override fun execute(log: Log<E>) {
    log.commit(index)
  }

  companion object {
    @JvmStatic fun <E : LogEntry> build(index: Long, reason: LogChangeReason): CommitCommand<E> = CommitCommand(index, reason)
  }
}
