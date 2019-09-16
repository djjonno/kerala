package org.elkd.core.log

import org.apache.log4j.Logger
import org.elkd.core.log.LogChangeEvent.APPEND
import org.elkd.core.log.LogChangeEvent.COMMIT

class LogChangeRegistry<E : LogEntry> constructor(log: LogInvoker<E>) {
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
      scopedOnCommitRegistrations[entry.uuid]?.forEach(Runnable::run)
      scopedOnCommitRegistrations.remove(entry.uuid)
    }

    override fun onAppend(index: Long, entry: E) {
      scopedOnAppendRegistrations[entry.uuid]?.forEach(Runnable::run)
      scopedOnAppendRegistrations.remove(entry.uuid)
    }
  }

  companion object {
    private val logger = Logger.getLogger(LogChangeRegistry::class.java)
  }
}
