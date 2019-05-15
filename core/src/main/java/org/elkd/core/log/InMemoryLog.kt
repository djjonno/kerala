package org.elkd.core.log

import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableList
import org.elkd.core.consensus.messages.Entry
import java.util.*

class InMemoryLog<E : LogEntry> : Log<E> {
  private val mLogStore: MutableList<E>
  private var mIndex: Long = 0
  override var commitIndex: Long = 0

  override val lastIndex: Long
    get() = mIndex - 1

  override val lastEntry: E
    get() = read(lastIndex)!!

  init {
    mLogStore = ArrayList()

    /* Default Log Record - prevents having to provide a log index that
       technically does not exist e.g index = -1 */
    append(Entry.NULL_ENTRY as E)
  }

  override fun append(entry: E): Long {
    Preconditions.checkNotNull(entry, "entry")

    insertOrReplace(mIndex.toInt(), entry)

    return mIndex++
  }

  override fun append(index: Long, entry: E): Long {
    Preconditions.checkState(0 <= index && index <= mIndex, "index")

    insertOrReplace(index.toInt(), entry)
    return index
  }

  override fun read(index: Long): E? {
    try {
      Preconditions.checkState(START_INDEX <= index && index <= mIndex)
      return mLogStore[index.toInt()]
    } catch (e: Exception) {
      return null
    }

  }

  override fun read(from: Long, to: Long): List<E> {
    Preconditions.checkState(START_INDEX <= from && from <= to)

    val subList = ArrayList<E>()
    for (i in from.toInt()..to) {
      val entry = read(i)
      if (entry != null) {
        subList.add(entry)
      }
    }

    return ImmutableList.copyOf(subList)
  }

  override fun commit(index: Long): CommitResult<E> {
    Preconditions.checkState(START_INDEX <= index && index < mIndex)
    val oldCommit = commitIndex
    commitIndex = index

    val entries = read(oldCommit + 1, index)

    return CommitResult(entries, index)
  }

  override fun revert(index: Long) {
    Preconditions.checkState(commitIndex < index)
    /* delete up to index */
    if (index <= mIndex) {
      while (index < mIndex) {
        mLogStore.removeAt((--mIndex).toInt())
      }
    }
  }

  override fun toString(): String {
    val committed = ArrayList<LogEntry>()
    for (i in START_INDEX..commitIndex) {
      committed.add(mLogStore.get(i.toInt()))
    }

    val builder = StringBuilder().append("Log[ ")
    mLogStore.stream().forEach { entry -> builder.append(" " + entry.term + " ") }
    builder.append(" ]")
    return builder.toString()
  }

  private fun insertOrReplace(index: Int, entry: E) {
    try {
      if (mLogStore[index] != null) {
        mLogStore.removeAt(index)
      }
    } catch (ignored: IndexOutOfBoundsException) {
    }

    mLogStore.add(index, entry)
  }

  companion object {
    private val START_INDEX = 0
  }
}
