package org.elkd.core.log

import org.apache.log4j.Logger
import org.elkd.core.log.commands.LogCommand
import org.elkd.core.log.ds.Log
import org.elkd.shared.annotations.Mockable
import java.util.concurrent.ExecutorService

@Mockable
class LogCommandExecutor<E : LogEntry> constructor(private val log: Log<E>,
                                                   private val threadPool: ExecutorService) {
  fun execute(command: LogCommand<E>) {
    threadPool.submit {
      LOGGER.info("executing $command")
      command.execute(log)
    }.get() /* wait for execution */
  }

  private companion object {
    var LOGGER: Logger = Logger.getLogger(LogCommandExecutor::class.java)
  }
}
