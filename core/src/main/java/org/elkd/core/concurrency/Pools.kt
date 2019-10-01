package org.elkd.core.concurrency

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Pools {

  private const val CLIENT_COMMAND_THREAD_POOL_SIZE = 1;
  private const val LOG_COMMAND_THREAD_POOL_SIZE = 1;
  private const val CONSENSUS_THREAD_POOL = 8;

  /**
   * Pool for client command execution
   */
  val clientCommandThreadPool: ExecutorService = Executors.newFixedThreadPool(
      CLIENT_COMMAND_THREAD_POOL_SIZE, ThreadFactoryProvider.create("client-cmd"))

  /**
   * Pool for logger command execution
   */
  val logCommandThreadPool: ExecutorService = Executors.newFixedThreadPool(
      LOG_COMMAND_THREAD_POOL_SIZE, ThreadFactoryProvider.create("logger-cmd"))

  /**
   * Pool for replication
   */
  val replicationThreadPool: ExecutorService = Executors.newFixedThreadPool(
      CONSENSUS_THREAD_POOL, ThreadFactoryProvider.create("repl"))

}
