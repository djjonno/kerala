package org.kerala.core.log.commands

import org.kerala.core.log.LogChangeReason
import org.kerala.core.log.LogEntry
import org.kerala.core.log.ds.Log

interface LogCommand<E : LogEntry> {
  val reason: LogChangeReason
  fun execute(log: Log<E>)
}
