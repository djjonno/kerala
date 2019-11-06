package org.kerala.core.log

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kerala.core.log.LogChangeEvent.APPEND
import org.kerala.core.log.LogChangeEvent.COMMIT
import org.kerala.core.log.exceptions.Event
import org.kerala.core.log.exceptions.LogChangeException
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LogChangeRegistry<E : LogEntry> constructor(
    log: LogInvoker<E>,
    coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope {
  private val listener: Listener<E> = Listener()
  private val scopedOnCommitRegistrations: MutableMap<Key, MutableList<CompletionHandler>> = mutableMapOf()
  private val scopedOnAppendRegistrations: MutableMap<Key, MutableList<CompletionHandler>> = mutableMapOf()

  init {
    log.registerListener(listener)

    initMonitor()
  }

  fun register(e: LogEntry, event: LogChangeEvent, onComplete: () -> Unit, onFailure: (f: LogChangeException) -> Unit, timeout: Duration? = null): CompletionHandler {
    return register(Key(uuid = e.uuid), event, onComplete, onFailure, timeout)
  }

  fun register(index: Long, event: LogChangeEvent, onComplete: () -> Unit, onFailure: (f: LogChangeException) -> Unit, timeout: Duration? = null): CompletionHandler {
    return register(Key(index = index), event, onComplete, onFailure, timeout)
  }

  private fun register(key: Key, event: LogChangeEvent, onComplete: () -> Unit, onFailure: (f: LogChangeException) -> Unit, timeout: Duration? = null): CompletionHandler {
    val handler = CompletionHandler(key, event, onComplete, onFailure, timeout)
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

  fun unregister(value: CompletionHandler, event: LogChangeEvent) {
    when (event) {
      COMMIT -> unregister(scopedOnCommitRegistrations, value)
      APPEND -> unregister(scopedOnAppendRegistrations, value)
    }
  }

  private fun unregister(map: MutableMap<Key, MutableList<CompletionHandler>>, value: CompletionHandler) {
    map[value.key]?.remove(value)
  }

  /**
   * Monitoring, if events take too long to laps, the registry cancels them.
   */
  private fun initMonitor() {
    launch {
      while (true) {
        delay(1_000)

        /* Check timeouts of append/commit registrations */
        val currentTime = System.currentTimeMillis()
        scopedOnCommitRegistrations
            .map { it.value }
            .union(scopedOnAppendRegistrations.map { it.value })
            .flatten()
            .filter { it.expiry != null && it.expiry <= currentTime && !it.isComplete() }
            .forEach { handler ->
              handler.onFailure(LogChangeException(Event.TIMEOUT))
              unregister(handler, handler.event)
            }
      }
    }
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
      internal val onFailure: (f: LogChangeException) -> Unit,
      private val timeout: Duration? = null
  ) {

    val expiry: Long? = timeout?.let { System.currentTimeMillis() + timeout.toMillis() }

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

    internal fun isComplete() = latch.count == 0L
  }
}
