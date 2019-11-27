package org.kerala.core.runtime.topic

import org.kerala.shared.logger
import java.util.concurrent.Executor

class TopicRegistry {
  private val registryById: MutableMap<String, Topic> = mutableMapOf()
  private val registryByNamespace: MutableMap<String, Topic> = mutableMapOf()
  private val listeners = mutableSetOf<Pair<Listener, Executor>>()

  val topics: List<Topic> get() = registryById.entries.map { it.value }
  val size = registryById.size

  fun add(topic: Topic) {
    if (getByTopic(topic) != null) {
      logger { d("$topic already exists - ignoring") }
      return
    }
    set(topic)
    notifyChange(topic, Listener.Event.ADDED)
    logger("topic registered $topic")
  }

  fun remove(topic: Topic) {
    getByTopic(topic)?.apply {
      unset(this)
      notifyChange(this, Listener.Event.REMOVED)
      logger("topic removed $topic")
    }
  }

  private fun set(topic: Topic) {
    registryById[topic.id] = topic
    registryByNamespace[topic.namespace] = topic
  }

  private fun unset(topic: Topic) {
    registryById.remove(topic.id)
    registryByNamespace.remove(topic.namespace)
  }

  fun getById(id: String): Topic? = registryById[id]
  fun getByNamespace(namespace: String): Topic? = registryByNamespace[namespace]
  private fun getByTopic(topic: Topic): Topic? = getById(topic.id)

  fun registerListener(listener: Listener, executor: Executor, rewind: Boolean = false) {
    listeners.add(Pair(listener, executor))

    if (rewind) {
      topics.forEach { notifyChange(it, Listener.Event.ADDED) }
    }
  }

  override fun toString() = "TopicRegistry($registryById)"

  private fun notifyChange(topic: Topic, event: Listener.Event) {
    listeners.forEach {
      it.second.execute { it.first.onChange(topic, event) }
    }
  }

  interface Listener {
    enum class Event {
      ADDED,
      REMOVED
    }

    fun onChange(topic: Topic, event: Event)
  }
}
