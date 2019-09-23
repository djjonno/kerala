package org.elkd.core.log

import org.elkd.core.log.ds.Log

class LogComponents (log: Log<LogEntry>) {
  val log
    get() = invoker

  val commandExecutor by lazy { LogCommandExecutor(invoker) }

  val changeRegistry by lazy { LogChangeRegistry(invoker) }

  private val invoker by lazy { LogInvoker(log) }

  override fun toString() = "Log(id=${log.id}, index=${log.lastIndex}, commit=${log.commitIndex})"
}
