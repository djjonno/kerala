package org.elkd.core.log

import org.apache.log4j.Logger
import org.elkd.core.log.LogChangeEvent.APPEND
import org.elkd.core.log.LogChangeEvent.COMMIT

class LogChangeRegistry<E : LogEntry> constructor(log: LogInvoker<E>) {
  private val listener: Listener<E> = Listener()

  private val onCommitRegistrations: MutableMap<String, MutableList<Runnable>> = mutableMapOf()
  private val onAppendRegistrations: MutableMap<String, MutableList<Runnable>> = mutableMapOf()

  init {
    log.registerListener(listener)
  }

  fun register(e: LogEntry, event: LogChangeEvent, onComplete: () -> Unit) {
    when (event) {
      COMMIT -> register(onCommitRegistrations, e.uuid, Runnable(onComplete))
      APPEND -> register(onAppendRegistrations, e.uuid, Runnable(onComplete))
    }
  }

  fun deregister(e: LogEntry) {
    onCommitRegistrations.remove(e.uuid)
    onAppendRegistrations.remove(e.uuid)
  }

  private fun register(map: MutableMap<String, MutableList<Runnable>>, key: String, value: Runnable) {
    if (!map.containsKey(key)) {
      map[key] = mutableListOf()
    }
    map[key]?.add(value)
  }

  private inner class Listener<E : LogEntry> : LogChangeListener<E> {
    override fun onCommit(index: Long, entry: E) {
      onCommitRegistrations[entry.uuid]?.forEach { it.run() }
      onCommitRegistrations.remove(entry.uuid)
    }

    override fun onAppend(index: Long, entry: E) {
      onAppendRegistrations[entry.uuid]?.forEach { it.run() }
      onAppendRegistrations.remove(entry.uuid)
    }
  }

  private companion object {
    private val log = Logger.getLogger(LogChangeRegistry::class.java)
  }
}
