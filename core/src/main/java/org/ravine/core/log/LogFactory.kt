package org.ravine.core.log

import org.ravine.core.log.ds.InMemoryLog

class LogFactory {
  fun createLog() = LogFacade(LogInvoker(InMemoryLog()))
}
