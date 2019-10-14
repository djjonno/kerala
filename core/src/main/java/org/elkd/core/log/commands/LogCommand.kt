package org.elkd.core.log.commands

import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.LogEntry
import org.elkd.core.log.ds.Log

interface LogCommand<E : LogEntry> {
  val reason: LogChangeReason
  fun execute(log: Log<E>)
}
