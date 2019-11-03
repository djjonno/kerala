package org.ravine.core.log

import java.util.concurrent.ExecutorService
import org.ravine.core.log.commands.LogCommand
import org.ravine.core.log.ds.Log
import org.ravine.shared.annotations.Mockable

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
