package org.kerala.core.log

import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.kerala.core.concurrency.Pools
import org.kerala.core.concurrency.asCoroutineScope
import org.kerala.core.consensus.messages.Entry
import org.kerala.core.log.ds.Log
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

class LogFacade(log: Log<Entry>) {

  val coroutineContext: CoroutineContext = Job()

  val log by lazy { invoker }
  val commandExecutor by lazy { LogCommandExecutor(invoker, threadPool) }
  val changeRegistry by lazy { LogChangeRegistry(invoker, threadPool.asCoroutineScope(coroutineContext)) }
  private val invoker by lazy { LogInvoker(log) }

  fun registerListener(listener: LogChangeListener<Entry>) = log.registerListener(listener)
  fun deregisterListener(listener: LogChangeListener<Entry>) = log.deregisterListener(listener)

  private val threadPool by lazy { Pools.createPool("log", 1) }

  /**
   * Perform a block category on the threadPool, synchronized by all other
   * log operations.
   */
  fun <T> readBlock(block: () -> T): T {
    return runBlocking(threadPool.asCoroutineDispatcher()) { block() }
  }
}
