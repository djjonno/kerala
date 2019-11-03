package org.ravine.core.runtime

import java.util.concurrent.Executor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch

typealias Block = (channel: NotificationsHub.Channel) -> Unit

/**
 * Pub/Sub singleton for runtime events.
 */
object NotificationsHub : CoroutineScope by GlobalScope {

  /**
   * Channels available for pub/sub.
   */
  enum class Channel(val id: String) {
    CONSENSUS_CHANGE("consensus-change")
  }

  private val subscriptions: MutableMap<Channel, MutableList<Pair<CoroutineDispatcher, Block>>> = mutableMapOf()

  fun pub(channel: Channel) {
    subscriptions[channel]?.forEach {
      launch(it.first) { it.second(channel) }
    }
  }

  fun sub(channel: Channel, executor: Executor, block: Block) {
    sub(channel, executor.asCoroutineDispatcher(), block)
  }

  fun sub(channel: Channel, dispatcher: CoroutineDispatcher, block: Block) {
    if (!subscriptions.containsKey(channel)) {
      subscriptions[channel] = mutableListOf()
    }
    subscriptions[channel]?.add(Pair(dispatcher, block))
  }
}
