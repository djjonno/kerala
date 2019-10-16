package org.elkd.core.runtime

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import org.apache.log4j.Logger

typealias Block = (channel: NotificationsHub.Channel) -> Unit

/**
 * Pub/Sub singleton for runtime events.
 */
object NotificationsHub : CoroutineScope by GlobalScope {

  private val LOGGER = Logger.getLogger(NotificationsHub::class.java)

  /**
   * Channels available for pub/sub.
   */
  enum class Channel(val id: String) {
    CONSENSUS_CHANGE("consensus-change")
  }

  private val subscriptions: MutableMap<Channel, MutableList<Pair<CoroutineDispatcher, Block>>> = mutableMapOf()

  fun pub(channel: Channel) {
    LOGGER.info("pub/${channel.id}")
    subscriptions[channel]?.forEach {
      launch (it.first) { it.second(channel) }
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
