package org.elkd.core.concurrency

import java.util.concurrent.ThreadFactory

object ThreadFactoryProvider {
  private const val THREAD_NAME = "pool-"
  private var globalThreadCount = 0

  fun create(name: String) = Factory(name)

  class Factory internal constructor(val name: String) : ThreadFactory {
    override fun newThread(r: Runnable) = Thread(r, "$THREAD_NAME$name-${globalThreadCount++}")
  }
}
