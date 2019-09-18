package org.elkd.core.log

import org.elkd.shared.annotations.Mockable

@Mockable
class LogFacade<E : LogEntry> constructor(val log: LogInvoker<E>) {
  val logCommandExecutor: LogCommandExecutor<E> = LogCommandExecutor(log)
  val logChangeRegistry: LogChangeRegistry<E> = LogChangeRegistry(log)

  fun registerListener(listener: LogChangeListener<E>) {
    log.registerListener(listener)
  }

  fun deregisterListener(listener: LogChangeListener<E>) {
    log.deregisterListener(listener)
  }
}
