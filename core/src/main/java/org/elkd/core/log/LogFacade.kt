package org.elkd.core.log

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.elkd.core.concurrency.Pools
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.ds.Log
import kotlin.coroutines.CoroutineContext

class LogFacade (log: Log<Entry>): CoroutineScope {

  val log by lazy { invoker }
  val commandExecutor by lazy { LogCommandExecutor(invoker, threadPool) }
  val changeRegistry by lazy { LogChangeRegistry(invoker, threadPool) }
  private val invoker by lazy { LogInvoker(log) }

  fun registerListener(listener: LogChangeListener<Entry>) = log.registerListener(listener)
  fun deregisterListener(listener: LogChangeListener<Entry>) = log.deregisterListener(listener)

  /*
   * Synchronization
   */

  private val threadPool by lazy { Pools.createPool("log", 1) }
  private val job: Job = Job()
  override val coroutineContext: CoroutineContext
    get() = job + threadPool.asCoroutineDispatcher()

  /**
   * Perform a block operation on the threadPool, synchronized by all other
   * log operations.
   */
  fun <T> readBlock(block: () -> T): T {
    return runBlocking(coroutineContext) { block() }
  }
}
