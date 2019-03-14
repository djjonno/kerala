package org.elkd.core.log.commands

import org.elkd.core.log.Log
import org.elkd.core.log.LogChangeReason

class CommitCommand<E> constructor(
    private val index: Long,
    override val reason: LogChangeReason
) : LogCommand<E> {

  override fun execute(log: Log<E>) {
    log.commit(index)
  }

  companion object {
    @JvmStatic fun <E> build(index: Long, reason: LogChangeReason): CommitCommand<E> = CommitCommand(index, reason)
  }
}
