package org.elkd.core.concurrency

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Pools {

  private const val CONSENSUS_THREAD_POOL = 1;
  private const val CLIENT_COMMAND_THREAD_POOL_SIZE = 1;
  private const val REPLICATION_THREAD_POOL = 8;

  /**
   * Pool for client controller execution.
   */
  val clientCommandThreadPool: ExecutorService = Executors.newFixedThreadPool(
      CLIENT_COMMAND_THREAD_POOL_SIZE, ThreadFactoryProvider.create("client-cmd"))

  /**
   * Single-threaded consensus thread pool.
   *
   * This allows all state components within the consensus package to
   * be without synchronization primitives.
   */
  val consensusThreadPool: ExecutorService = Executors.newFixedThreadPool(
      CONSENSUS_THREAD_POOL, ThreadFactoryProvider.create("consensus"))

  /**
   * Pool for replication.
   */
  val replicationThreadPool: ExecutorService = Executors.newFixedThreadPool(
      REPLICATION_THREAD_POOL, ThreadFactoryProvider.create("replication"))

  /**
   * Pool for logger controller execution
   */
  fun createPool(name: String, size: Int = 1): ExecutorService = Executors.newFixedThreadPool(
      size, ThreadFactoryProvider.create(name))

}
