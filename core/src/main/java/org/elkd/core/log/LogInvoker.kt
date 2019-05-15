package org.elkd.core.log

import org.elkd.shared.annotations.Mockable
import java.util.HashSet

@Mockable
class LogInvoker<E : LogEntry>(private val log: Log<E>) : Log<E> {
  private val listeners = HashSet<LogChangeListener<E>>()

  override val commitIndex: Long
    get() = log.commitIndex

  override val lastIndex: Long
    get() = log.lastIndex

  override val lastEntry: E
    get() = log.lastEntry

  override fun append(entry: E): Long {
    val index = log.append(entry)
    onAppend(entry)
    return index
  }

  override fun append(index: Long, entry: E): Long {
    val append = log.append(index, entry)
    onAppend(entry)
    return append
  }

  override fun read(index: Long): E? {
    return log.read(index)
  }

  override fun read(from: Long, to: Long): List<E> {
    return log.read(from, to)
  }

  override fun commit(index: Long): CommitResult<E> {
    val result = log.commit(index)
    result.committed.forEach { onCommit(it) }
    return result
  }

  override fun revert(index: Long) {
    log.revert(index)
  }

  fun registerListener(listener: LogChangeListener<E>) {
    listeners.add(listener)
  }

  fun deregisterListener(listener: LogChangeListener<E>) {
    listeners.remove(listener)
  }

  private fun onCommit(entry: E) {
    listeners.forEach { it.onCommit(entry) }
  }

  private fun onAppend(entry: E) {
    listeners.forEach { it.onAppend(entry) }
  }
}
