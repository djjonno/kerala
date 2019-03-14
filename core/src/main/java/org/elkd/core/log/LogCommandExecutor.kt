package org.elkd.core.log

import org.apache.log4j.Logger
import org.elkd.core.log.commands.LogCommand
import org.elkd.shared.annotations.Mockable

@Mockable
class LogCommandExecutor<E> constructor(private val log: Log<E>) {
  private val LOG = Logger.getLogger(LogCommandExecutor::class.java)

  fun execute(command: LogCommand<E>) {
    LOG.info("executing ${command.javaClass.name} for ${command.reason}")
    command.execute(log)
  }
}
