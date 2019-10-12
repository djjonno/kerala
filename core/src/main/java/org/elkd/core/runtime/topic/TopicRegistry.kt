package org.elkd.core.runtime.topic

import org.apache.log4j.Logger
import java.util.concurrent.Executor

class TopicRegistry {
  private val registry: MutableMap<String, Topic> = mutableMapOf()
  private val listeners = mutableSetOf<Pair<Listener, Executor>>()

  val topics: List<Topic> get() = registry.entries.map { it.value }
  val namespaces: List<String> get() = registry.entries.map { it.value.namespace }
  val size = topics.size

  fun add(topic: Topic) {
    if (get(topic) != null) {
      LOGGER.warn("$topic already exists, ignoring op.")
      return
    }
    registry[topic.id] = topic
    notifyChange(topic, Listener.Event.ADDED)
    LOGGER.info("new topic $topic registered")
  }

  fun remove(topic: Topic) {
    get(topic)?.apply {
      registry.remove(id)
      notifyChange(this, Listener.Event.REMOVED)
      LOGGER.info("topic removed: $topic")
    }
  }

  fun get(id: String): Topic? = registry[id]
  fun get(topic: Topic): Topic? = get(topic.id)
  fun findByNamespace(namespace: String): Topic? = topics.firstOrNull { it.namespace == namespace }

  fun registerListener(listener: Listener, executor: Executor, rewind: Boolean = false) {
    listeners.add(Pair(listener, executor))

    if (rewind) {
      topics.forEach { notifyChange(it, Listener.Event.ADDED) }
    }
  }

  override fun toString() = "TopicRegistry($registry)"
  operator fun contains(topicId: String): Boolean {
    return registry.containsKey(topicId)
  }

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

  companion object {
    private var LOGGER = Logger.getLogger(TopicRegistry::class.java)
  }
}
