package org.elkd.core.log.commands

import org.elkd.core.log.Log
import org.elkd.core.log.LogChangeReason

class AppendCommand<E> constructor(
    private val entries: List<E>,
    override val reason: LogChangeReason) : LogCommand<E> {

  override fun execute(log: Log<E>) {
    entries.forEach { log.append(it) }
  }

  companion object {
    fun <E> build(entry: E, reason: LogChangeReason): AppendCommand<E> = build(listOf(entry), reason)
    fun <E> build(entries: List<E>, reason: LogChangeReason): AppendCommand<E> = AppendCommand(entries, reason)
  }
}
