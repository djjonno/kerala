package org.elkd.core.log

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.elkd.core.log.LogChangeEvent.APPEND
import org.elkd.core.log.LogChangeEvent.COMMIT

class LogChangeRegistry<E : LogEntry> constructor(
    log: LogInvoker<E>
) {
  private val listener: Listener<E> = Listener()
  private val scopedOnCommitRegistrations: MutableMap<Key, MutableList<CompletionHandler>> = mutableMapOf()
  private val scopedOnAppendRegistrations: MutableMap<Key, MutableList<CompletionHandler>> = mutableMapOf()

  init {
    log.registerListener(listener)
  }

  fun cancelCommitRegistrations(reason: CancellationReason) {
    scopedOnCommitRegistrations.forEach {
      it.value.forEach { handler ->
        unregister(handler, COMMIT)
        handler.onFailure(reason)
      }
    }
  }

  fun register(e: LogEntry, event: LogChangeEvent, onComplete: () -> Unit, onFailure: (f: CancellationReason) -> Unit): CompletionHandler {
    return register(Key(uuid = e.uuid), event, onComplete, onFailure)
  }

  fun register(index: Long, event: LogChangeEvent, onComplete: () -> Unit, onFailure: (f: CancellationReason) -> Unit): CompletionHandler {
    return register(Key(index = index), event, onComplete, onFailure)
  }

  private fun register(key: Key, event: LogChangeEvent, onComplete: () -> Unit, onFailure: (f: CancellationReason) -> Unit): CompletionHandler {
    val handler = CompletionHandler(key, event, onComplete, onFailure)
    when (event) {
      COMMIT -> registerToMap(scopedOnCommitRegistrations, handler)
      APPEND -> registerToMap(scopedOnAppendRegistrations, handler)
    }
    return handler
  }

  private fun registerToMap(map: MutableMap<Key, MutableList<CompletionHandler>>, handler: CompletionHandler) {
    if (!map.containsKey(handler.key)) {
      map[handler.key] = mutableListOf()
    }
    map[handler.key]?.add(handler)
  }

  private fun unregister(value: CompletionHandler, event: LogChangeEvent) {
    when (event) {
      COMMIT -> unregister(scopedOnCommitRegistrations, value)
      APPEND -> unregister(scopedOnAppendRegistrations, value)
    }
  }

  private fun unregister(map: MutableMap<Key, MutableList<CompletionHandler>>, value: CompletionHandler) {
    map[value.key]?.remove(value)
  }

  private inner class Listener<E : LogEntry> : LogChangeListener<E> {
    override fun onCommit(index: Long, entry: E) {
      withKeys(index, entry) { i, u ->
        scopedOnCommitRegistrations[i]?.forEach { it.done() }
        scopedOnCommitRegistrations.remove(i)
        scopedOnCommitRegistrations[u]?.forEach { it.done() }
        scopedOnCommitRegistrations.remove(u)
      }
    }

    override fun onAppend(index: Long, entry: E) {
      withKeys(index, entry) { i, u ->
        scopedOnAppendRegistrations[i]?.forEach { it.done() }
        scopedOnAppendRegistrations.remove(i)
        scopedOnAppendRegistrations[u]?.forEach { it.done() }
        scopedOnAppendRegistrations.remove(u)
      }
    }

    private fun withKeys(index: Long, entry: E, block: (i: Key, u: Key) -> Unit) {
      val indexKey = Key(index = index)
      val uuidKey = Key(uuid = entry.uuid)
      block(indexKey, uuidKey)
    }
  }

  /**
   * Key to scope index or uuid to CompletionHandler.
   */
  data class Key(val index: Long? = null, val uuid: String? = null)

  /**
   * CompletionHandler which contains contextual data of a given listener.
   */
  inner class CompletionHandler(
      internal val key: Key,
      internal val event: LogChangeEvent,
      internal val onComplete: () -> Unit,
      internal val onFailure: (f: CancellationReason) -> Unit
  ) {
    private val latch = CountDownLatch(1)
    fun get(timeout: Long, unit: TimeUnit) {
      try {
        latch.await(timeout, unit)
      } catch (e: Exception) {
        unregister(this, event)
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
