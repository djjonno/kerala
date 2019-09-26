package org.elkd.core.log

import org.elkd.core.log.ds.InMemoryLog

class LogFactory {
  fun createLog() = LogFacade(LogInvoker(InMemoryLog()))
}
