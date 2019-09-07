package org.elkd.core.concurrency

import kotlinx.coroutines.Dispatchers
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object Pools {

  private const val CLIENT_COMMAND_THREAD_POOL_SIZE = 1;
  private const val LOG_COMMAND_THREAD_POOL_SIZE = 1;

  /**
   * Pool for client command execution
   */
  val clientCommandThreadPool: Executor = Executors.newFixedThreadPool(
      CLIENT_COMMAND_THREAD_POOL_SIZE, ThreadFactoryProvider.create("client-cmd"))

  /**
   * Pool for log command execution
   */
  val logCommandThreadPool: Executor = Executors.newFixedThreadPool(
      LOG_COMMAND_THREAD_POOL_SIZE, ThreadFactoryProvider.create("log-cmd"))

  /**
   * Pool for replication
   */
  val replicationThreadPool = Dispatchers.IO

}
