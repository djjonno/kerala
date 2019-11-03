package org.ravine.core.log

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.ravine.core.concurrency.Pools
import org.ravine.core.consensus.messages.Entry
import org.ravine.core.log.ds.Log
import org.ravine.core.runtime.NotificationsHub

class LogFacade(log: Log<Entry>) {

  val log by lazy { invoker }
  val commandExecutor by lazy { LogCommandExecutor(invoker, threadPool) }
  val changeRegistry by lazy { LogChangeRegistry(invoker) }
  private val invoker by lazy { LogInvoker(log) }

  fun registerListener(listener: LogChangeListener<Entry>) = log.registerListener(listener)
  fun deregisterListener(listener: LogChangeListener<Entry>) = log.deregisterListener(listener)

  private val threadPool by lazy { Pools.createPool("log", 1) }

  init {
    /* Listen for consensus changes to notify log components. */
    NotificationsHub.sub(NotificationsHub.Channel.CONSENSUS_CHANGE, threadPool.asCoroutineDispatcher()) {
      changeRegistry.cancelCommitRegistrations(LogChangeRegistry.CancellationReason.CANNOT_COMMIT)
    }
  }

  /**
   * Perform a block category on the threadPool, synchronized by all other
   * log operations.
   */
  fun <T> readBlock(block: () -> T): T {
    return runBlocking(threadPool.asCoroutineDispatcher()) { block() }
  }
}
