package org.elkd.core.log

import org.apache.log4j.Logger
import org.elkd.core.log.LogChangeEvent.*

class LogChangeRegistry<E : LogEntry> constructor(log: LogInvoker<E>) {
  private val listener: Listener<E> = Listener()

  private val onCommitRegistrations: MutableMap<String, MutableList<Runnable>> = mutableMapOf()
  private val onAppendRegistrations: MutableMap<String, MutableList<Runnable>> = mutableMapOf()

  init {
    log.registerListener(listener)
  }

  fun register(e: LogEntry, event: LogChangeEvent, block: () -> Unit) {
    when (event) {
      COMMIT -> register(onCommitRegistrations, e.id, Runnable(block))
      APPEND -> register(onAppendRegistrations, e.id, Runnable(block))
    }
  }

  fun clear() {
    onCommitRegistrations.clear()
    onAppendRegistrations.clear()
  }

  private fun register(map: MutableMap<String, MutableList<Runnable>>, key: String, value: Runnable) {
    if (!map.containsKey(key)) {
      map[key] = mutableListOf()
    }
    map[key]?.add(value)
  }

  private inner class Listener<E : LogEntry> : LogChangeListener<E> {
    override fun onCommit(index: Long, entry: E) {
      log.info("committed: $entry @ $index")
    }

    override fun onAppend(index: Long, entry: E) {
      log.info("appended: $entry @ $index")
    }
  }

  private companion object {
    private val log = Logger.getLogger(LogChangeRegistry::class.java)
  }
}
