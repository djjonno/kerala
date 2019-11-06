package org.kerala.core.log.ds

import org.kerala.core.log.CommitResult
import org.kerala.core.log.LogEntry

interface Log<E : LogEntry> {
  val id: String
  val commitIndex: Long
  val lastIndex: Long
  val lastEntry: E

  fun append(entry: E): Long

  fun append(index: Long, entry: E): Long

  fun read(index: Long): E?

  fun read(from: Long, to: Long): List<E>

  fun readSnapshot(from: Long, to: Long): LogSnapshot<E>

  fun commit(index: Long): CommitResult<E>

  fun revert(index: Long)
}
