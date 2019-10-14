package org.elkd.core.concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object Pools {

  private const val CONSENSUS_THREAD_POOL = 1;
  private const val CLIENT_REQUEST_THREAD_POOL_SIZE = 1;
  private const val REPLICATION_THREAD_POOL = 8;

  /**
   * Pool for servicing client requests.
   */
  val clientRequestPool: ExecutorService = Executors.newFixedThreadPool(
      CLIENT_REQUEST_THREAD_POOL_SIZE, ThreadFactoryProvider.create("client-req"))

  /**
   * Single-threaded consensus thread threadPool.
   *
   * This allows all state components within the consensus package to
   * be without synchronization primitives.
   */
  val consensusPool: ExecutorService = Executors.newFixedThreadPool(
      CONSENSUS_THREAD_POOL, ThreadFactoryProvider.create("consensus"))

  /**
   * Pool for replication.
   */
  val replicationPool: ExecutorService = Executors.newFixedThreadPool(
      REPLICATION_THREAD_POOL, ThreadFactoryProvider.create("replication"))

  /**
   * Create a new Pool with size.
   */
  fun createPool(name: String, size: Int = 1): ExecutorService = Executors.newFixedThreadPool(
      size, ThreadFactoryProvider.create(name))

}

fun ExecutorService.asCoroutineScope(context: CoroutineContext = EmptyCoroutineContext) : CoroutineScope {
  return CoroutineScope(asCoroutineDispatcher() + Job(context[Job]))
}
