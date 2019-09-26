package org.elkd.core.concurrency

import java.util.concurrent.ThreadFactory

object ThreadFactoryProvider {
  private const val THREAD_NAME = "pool-"
  private var globalPoolCount = 0
  private var globalThreadCount = 0

  fun create(name: String) = Factory(name, ++globalPoolCount)

  class Factory internal constructor(val name: String, val poolCount: Int) : ThreadFactory {
    override fun newThread(r: Runnable) = Thread(r, "$THREAD_NAME${poolCount}-$name-${globalThreadCount++}")
  }
}
