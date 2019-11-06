package org.kerala.core.log

import org.kerala.core.log.ds.InMemoryLog

class LogFactory {
  fun createLog() = LogFacade(LogInvoker(InMemoryLog()))
}
