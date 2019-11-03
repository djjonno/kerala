package org.ravine.core.log.commands

import org.ravine.core.log.LogChangeReason
import org.ravine.core.log.LogEntry
import org.ravine.core.log.ds.Log

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
