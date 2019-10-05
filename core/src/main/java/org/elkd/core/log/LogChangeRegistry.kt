package org.elkd.core.log

import org.apache.log4j.Logger
import org.elkd.core.log.LogChangeEvent.APPEND
import org.elkd.core.log.LogChangeEvent.COMMIT
import java.lang.Exception
import java.util.concurrent.ExecutorService

class LogChangeRegistry<E : LogEntry> constructor(log: LogInvoker<E>,
                                                  private val threadPool: ExecutorService) {
  private val listener: Listener<E> = Listener()

  private val scopedOnCommitRegistrations: MutableMap<String, MutableList<Runnable>> = mutableMapOf()
  private val scopedOnAppendRegistrations: MutableMap<String, MutableList<Runnable>> = mutableMapOf()

  init {
    log.registerListener(listener)
  }

  fun register(e: LogEntry, event: LogChangeEvent, onComplete: () -> Unit) {
    when (event) {
      COMMIT -> register(scopedOnCommitRegistrations, e.uuid, Runnable(onComplete))
      APPEND -> register(scopedOnAppendRegistrations, e.uuid, Runnable(onComplete))
    }
  }

  private fun register(map: MutableMap<String, MutableList<Runnable>>, key: String, value: Runnable) {
    if (!map.containsKey(key)) {
      map[key] = mutableListOf()
    }
    map[key]?.add(value)
  }

  private inner class Listener<E : LogEntry> : LogChangeListener<E> {
    override fun onCommit(index: Long, entry: E) {
      scopedOnCommitRegistrations[entry.uuid]?.forEach { it -> safelyExecute(it) }
      scopedOnCommitRegistrations.remove(entry.uuid)
    }

    override fun onAppend(index: Long, entry: E) {
      scopedOnAppendRegistrations[entry.uuid]?.forEach { it -> safelyExecute(it) }
      scopedOnAppendRegistrations.remove(entry.uuid)
    }

    private fun safelyExecute(runnable: Runnable) {
      try {
        threadPool.execute(runnable)
      } catch (e: Exception) {
        LOGGER.error("Change registry failed to execute listener", e)
      }
    }
  }

  private companion object {
    val LOGGER = Logger.getLogger(this::class.java)
  }
}
