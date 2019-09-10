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
    onAppend(index, entry)
    return index
  }

  override fun read(index: Long): E? {
    return log.read(index)
  }

  override fun read(from: Long, to: Long): List<E> {
    return log.read(from, to)
  }

  override fun append(index: Long, entry: E): Long {
    val append = log.append(index, entry)
    onAppend(append, entry)
    return append
  }

  override fun commit(index: Long): CommitResult<E> {
    val result = log.commit(index)
    result.committed.forEachIndexed { i, e -> onCommit((result.commitIndex - (result.committed.size - 1)) + i, e) }
    return result
  }

  override fun revert(index: Long) {
    log.revert(index)
  }

  internal fun registerListener(listener: LogChangeListener<E>) {
    listeners.add(listener)
  }

  internal fun deregisterListener(listener: LogChangeListener<E>) {
    listeners.remove(listener)
  }

  private fun onCommit(index: Long, entry: E) {
    listeners.forEach { it.onCommit(index, entry) }
  }

  private fun onAppend(index: Long, entry: E) {
    listeners.forEach { it.onAppend(index, entry) }
  }

  override fun toString(): String {
    return "LogInvoker(log=$log)"
  }
}
