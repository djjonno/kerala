package org.elkd.core.log

import org.elkd.core.concurrency.Pools
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.ds.Log

class LogFacade (log: Log<Entry>) {
  val log by lazy { invoker }
  val commandExecutor by lazy { LogCommandExecutor(invoker, threadPool) }
  val changeRegistry by lazy { LogChangeRegistry(invoker, threadPool) }
  /* This threadPool is used exclusively for log operations. */
  private val threadPool by lazy { Pools.createPool("log", 1) }
  private val invoker by lazy { LogInvoker(log) }

  fun registerListener(listener: LogChangeListener<Entry>) = log.registerListener(listener)
  fun deregisterListener(listener: LogChangeListener<Entry>) = log.deregisterListener(listener)

  override fun toString() = "Log(id=${log.id}, index=${log.lastIndex}, commit=${log.commitIndex})"
}
