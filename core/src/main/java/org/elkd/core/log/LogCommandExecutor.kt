package org.elkd.core.log

import org.elkd.core.log.commands.LogCommand
import org.elkd.core.log.ds.Log
import org.elkd.shared.annotations.Mockable
import java.util.concurrent.ExecutorService

@Mockable
class LogCommandExecutor<E : LogEntry> constructor(
    private val log: Log<E>,
    private val threadPool: ExecutorService
) {
  fun execute(command: LogCommand<E>) {
    threadPool.submit {
      command.execute(log)
    }
  }
}
