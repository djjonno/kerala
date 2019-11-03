package org.ravine.core.log.commands

import org.ravine.core.log.LogChangeReason
import org.ravine.core.log.LogEntry
import org.ravine.core.log.ds.Log

interface LogCommand<E : LogEntry> {
  val reason: LogChangeReason
  fun execute(log: Log<E>)
}
