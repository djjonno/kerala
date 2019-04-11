package org.elkd.core.consensus

import java.util.concurrent.ThreadFactory

class ThreadFactory {
  companion object {
    private const val RAFT_THREAD_DOMAIN = "raft-domain"
    private var raftThreadCount = 1

    @JvmStatic fun raftThreadFactory(): java.util.concurrent.ThreadFactory {
      return object : ThreadFactory {
        override fun newThread(r: Runnable?) = Thread(r, "$RAFT_THREAD_DOMAIN-${raftThreadCount++}")
      }
    }
  }
}
