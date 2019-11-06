package org.kerala.core.log

import java.util.concurrent.ExecutorService
import org.kerala.core.log.commands.LogCommand
import org.kerala.core.log.ds.Log
import org.kerala.shared.annotations.Mockable

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
