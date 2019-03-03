package org.elkd.core.log.commands

import org.elkd.core.log.Log
import org.elkd.core.log.LogChangeReason

interface LogCommand<E> {
  val reason: LogChangeReason
  fun execute(log: Log<E>)
}
