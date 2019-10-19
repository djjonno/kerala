package org.elkd.core.log

import org.elkd.core.log.LogChangeEvent.APPEND
import org.elkd.core.log.LogChangeEvent.COMMIT
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LogChangeRegistry<E : LogEntry> constructor(
    log: LogInvoker<E>
) {
  private val listener: Listener<E> = Listener()
  private val scopedOnCommitRegistrations: MutableMap<String, MutableList<CompletionHandler>> = mutableMapOf()
  private val scopedOnAppendRegistrations: MutableMap<String, MutableList<CompletionHandler>> = mutableMapOf()

  init {
    log.registerListener(listener)
  }

  fun cancelCommitRegistrations(reason: CancellationReason) {
    scopedOnCommitRegistrations.forEach {
      it.value.forEach { handler ->
        deregister(handler, COMMIT)
        handler.onFailure(reason)
      }
    }
  }

  fun register(e: LogEntry, event: LogChangeEvent, onComplete: () -> Unit, onFailure: (f: CancellationReason) -> Unit): CompletionHandler {
    val handler = CompletionHandler(e, event, onComplete, onFailure)
    when (event) {
      COMMIT -> register(scopedOnCommitRegistrations, e.uuid, handler)
      APPEND -> register(scopedOnAppendRegistrations, e.uuid, handler)
    }
    return handler
  }

  private fun register(map: MutableMap<String, MutableList<CompletionHandler>>, key: String, value: CompletionHandler) {
    if (!map.containsKey(key)) {
      map[key] = mutableListOf()
    }
    map[key]?.add(value)
  }

  private fun deregister(value: CompletionHandler, event: LogChangeEvent) {
    when (event) {
      COMMIT -> deregister(scopedOnCommitRegistrations, value)
      APPEND -> deregister(scopedOnAppendRegistrations, value)
    }
  }

  private fun deregister(map: MutableMap<String, MutableList<CompletionHandler>>, value: CompletionHandler) {
    map[value.entry.uuid]?.remove(value)
  }

  private inner class Listener<E : LogEntry> : LogChangeListener<E> {
    override fun onCommit(index: Long, entry: E) {
      scopedOnCommitRegistrations[entry.uuid]?.forEach { it.done() }
      scopedOnCommitRegistrations.remove(entry.uuid)
    }

    override fun onAppend(index: Long, entry: E) {
      scopedOnAppendRegistrations[entry.uuid]?.forEach { it.done() }
      scopedOnAppendRegistrations.remove(entry.uuid)
    }
  }

  inner class CompletionHandler(internal val entry: LogEntry,
                                internal val event: LogChangeEvent,
                                internal val onComplete: () -> Unit,
                                internal val onFailure: (f: CancellationReason) -> Unit) {
    private val latch = CountDownLatch(1)
    fun get(timeout: Long, unit: TimeUnit) {
      try {
        latch.await(timeout, unit)
      } catch (e: Exception) {
        deregister(this, event)
      }
    }

    internal fun done() {
      onComplete()
      latch.countDown()
    }
  }

  enum class CancellationReason {
    /**
     * LogChangeEvent.COMMIT event cannot occur on this node state.
     */
    CANNOT_COMMIT
  }
}
