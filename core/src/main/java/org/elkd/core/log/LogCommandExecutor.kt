package org.elkd.core.log

import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.log.commands.LogCommand
import org.elkd.shared.annotations.Mockable

@Mockable
class LogCommandExecutor<E : LogEntry> constructor(private val log: Log<E>) {
  private val LOG = Logger.getLogger(LogCommandExecutor::class.java)

  fun execute(command: LogCommand<E>) {
    Pools.logCommandThreadPool.execute {
      LOG.info("Executing ${command.javaClass.name} for ${command.reason}")
      command.execute(log)
    }
  }
}
