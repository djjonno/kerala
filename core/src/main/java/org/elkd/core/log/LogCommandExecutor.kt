package org.elkd.core.log

import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.log.commands.LogCommand
import org.elkd.core.log.ds.Log
import org.elkd.shared.annotations.Mockable

@Mockable
class LogCommandExecutor<E : LogEntry> constructor(private val log: Log<E>) {
  fun execute(command: LogCommand<E>) {
    LOGGER.info("Executing $command")
    Pools.logCommandThreadPool.submit {
      command.execute(log)
    }.get()
  }

  private companion object {
    var LOGGER: Logger = Logger.getLogger(LogCommandExecutor::class.java)
  }
}
