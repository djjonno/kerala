package org.kerala.core.log

interface LogChangeListener<E : LogEntry> {
  fun onCommit(index: Long, entry: E) {}
  fun onAppend(index: Long, entry: E) {}
}
