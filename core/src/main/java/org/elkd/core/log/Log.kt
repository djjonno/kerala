package org.elkd.core.log

interface Log<E> {
  val commitIndex: Long
  val lastIndex: Long

  fun append(entry: E): Long

  fun append(index: Long, entry: E): Long

  fun read(index: Long): E

  fun read(from: Long, to: Long): List<E>

  fun commit(index: Long): CommitResult<E>

  fun revert(index: Long)
}
