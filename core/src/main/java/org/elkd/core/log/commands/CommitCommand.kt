package org.elkd.core.log.commands

import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.LogEntry
import org.elkd.core.log.ds.Log

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
