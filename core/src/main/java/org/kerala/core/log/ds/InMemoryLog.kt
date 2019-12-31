package org.kerala.core.log.ds

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Preconditions.checkState
import org.kerala.core.log.CommitResult
import org.kerala.core.log.LogEntry
import org.kerala.shared.logger
import java.util.UUID

class InMemoryLog<E : LogEntry> : Log<E> {
  private val logStore: MutableList<E> = ArrayList<E>()
  private var index: Long = 0

  override val id: String = UUID.randomUUID().toString()

  override var commitIndex: Long = -1

  override val lastIndex: Long
    get() = index - 1

  override val lastEntry: E?
    get() = read(lastIndex)

  override val isEmpty: Boolean
    get() = logStore.isEmpty()

  override fun append(entry: E): Long {
    checkNotNull(entry, "entry")

    insertOrReplace(index.toInt(), entry)

    return index++
  }

  override fun append(index: Long, entry: E): Long {
    checkState(index in 0..this.index, "index")

    insertOrReplace(index.toInt(), entry)
    return index
  }

  override fun read(index: Long): E? {
    return try {
      checkState(index in START_INDEX..this.index)
      logStore[index.toInt()]
    } catch (e: Exception) {
      null
    }
  }

  override fun read(from: Long, to: Long): List<E> {
    checkState(from in START_INDEX..to)

    return (from.toInt()..to).mapNotNull(::read)
  }

  override fun readSnapshot(from: Long, to: Long): LogSnapshot<E> {
    val entries = read(from, to)
    val previousEntry = read(from - 1)

    val logSnapshot = LogSnapshot(
        prevLogTerm = previousEntry?.term ?: 0,
        prevLogIndex = from - 1,
        commitIndex = commitIndex,
        entries = entries
    )
    logger("reading snapshot $logSnapshot")
    return logSnapshot
  }

  override fun commit(index: Long): CommitResult<E> {
    checkState(index in START_INDEX until this.index)

    if (commitIndex == index) {
      return CommitResult(emptyList(), commitIndex)
    }

    val oldCommit = commitIndex
    commitIndex = index
    val entries = read(oldCommit + 1, index)

    return CommitResult(entries, index)
  }

  override fun revert(index: Long) {
    checkState(commitIndex < index)
    /* delete up to index */
    if (index <= this.index) {
      while (index < this.index) {
        logStore.removeAt((--this.index).toInt())
      }
    }
  }

  private fun insertOrReplace(index: Int, entry: E) {
    try {
      logStore.removeAt(index)
    } catch (ignored: IndexOutOfBoundsException) {
    }

    logStore.add(index, entry)
  }

  override fun toString(): String {
    return "Log(size=${logStore.size}, index=$index, lastIndex=$lastIndex, lastEntry=$lastEntry)"
  }

  companion object {
    private const val START_INDEX = 0
  }
}
