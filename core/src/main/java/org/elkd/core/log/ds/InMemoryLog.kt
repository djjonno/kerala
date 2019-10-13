package org.elkd.core.log.ds

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Preconditions.checkState
import com.google.common.collect.ImmutableList
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.CommitResult
import org.elkd.core.log.LogEntry
import java.util.UUID

class InMemoryLog<E : LogEntry> : Log<E> {
  private val logStore: MutableList<E>
  private var index: Long = 0

  /**
   * This is a uuid used for identifying this particular logFacade instance.
   */
  override val id: String = UUID.randomUUID().toString()

  override var commitIndex: Long = 0

  override val lastIndex: Long
    get() = index - 1

  override val lastEntry: E
    get() = read(lastIndex)!!

  init {
    logStore = ArrayList<E>()

    /* Default Log Record - prevents having to provide a logger index that
       technically does not exist e.g index = -1 */
    append(Entry.NULL_ENTRY as E)
  }

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

  override fun readSnapshot(from: Long, to: Long) : LogSnapshot<E> {
    val entries = read(from, to)
    val initial = read(from - 1)!!

    return LogSnapshot(
        prevLogTerm = initial.term,
        prevLogIndex = from - 1,
        commitIndex = commitIndex,
        entries = entries
    )
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
    return "Log(size=${logStore.size}, index=$index)"
  }

  companion object {
    private const val START_INDEX = 0
  }
}
