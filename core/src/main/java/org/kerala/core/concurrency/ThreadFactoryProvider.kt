package org.kerala.core.concurrency

import java.util.concurrent.ThreadFactory

object ThreadFactoryProvider {
  private const val THREAD_NAME = "pool-"
  private var globalPoolCount = 0
  private var globalThreadCount = 0

  fun create(name: String) = Factory(name, ++globalPoolCount)

  class Factory internal constructor(private val name: String, private val poolCount: Int) : ThreadFactory {
    override fun newThread(r: Runnable) = Thread(r, "$THREAD_NAME$poolCount-$name-${globalThreadCount++}")
  }
}
