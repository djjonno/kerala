package org.elkd.core.log

import org.elkd.shared.annotations.Mockable

@Mockable
class LogProvider<E : LogEntry> constructor(val log: LogInvoker<E>) {
  val logCommandExecutor: LogCommandExecutor<E> = LogCommandExecutor(log)
  val logChangeNotifier: LogChangeNotifier<E> = LogChangeNotifier(log)
}
