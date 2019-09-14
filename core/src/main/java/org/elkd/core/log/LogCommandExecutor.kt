package org.elkd.core.log

import org.elkd.core.concurrency.Pools
import org.elkd.core.log.commands.LogCommand
import org.elkd.shared.annotations.Mockable

@Mockable
class LogCommandExecutor<E : LogEntry> constructor(private val log: Log<E>) {
  fun execute(command: LogCommand<E>) {
    Pools.logCommandThreadPool.submit {
      command.execute(log)
    }.get()
  }
}
