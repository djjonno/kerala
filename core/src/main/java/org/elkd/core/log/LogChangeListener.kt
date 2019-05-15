package org.elkd.core.log

interface LogChangeListener<E : LogEntry> {
  fun onCommit(entry: E)
  fun onAppend(entry: E)
}
