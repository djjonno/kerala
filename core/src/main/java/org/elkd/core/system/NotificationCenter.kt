package org.elkd.core.system

import org.apache.log4j.Logger
import java.util.concurrent.Executor

/**
 * PubSub singleton for system events.
 */
object NotificationCenter {

  private val log = Logger.getLogger(NotificationCenter::class.java)

  /**
   * Channels available for pub/sub.
   */
  enum class Channel(val id: String) {
    RAFT_STATE_CHANGE("raft-state-change")
  }

  private val subscriptions: MutableMap<Channel, MutableList<Pair<Executor, Runnable>>> = mutableMapOf()

  fun pub(channel: Channel) {
    log.info("pub:${channel.id}")
    subscriptions[channel]?.forEach {
      it.first.execute(it.second)
    }
  }

  fun sub(channel: Channel, executor: Executor, block: (channel: Channel) -> Unit) {
    if (!subscriptions.containsKey(channel)) {
      subscriptions[channel] = mutableListOf()
    }

    subscriptions[channel]?.add(Pair(executor, Runnable { block(channel) }))
  }
}
