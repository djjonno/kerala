package org.elkd.core.runtime.topic

import org.apache.log4j.Logger
import java.util.concurrent.Executor

class TopicRegistry {
  private val registryById: MutableMap<String, Topic> = mutableMapOf()
  private val registryByNamespace: MutableMap<String, Topic> = mutableMapOf()
  private val listeners = mutableSetOf<Pair<Listener, Executor>>()

  val topics: List<Topic> get() = registryById.entries.map { it.value }
  val size = registryById.size

  fun add(topic: Topic) {
    if (getByTopic(topic) != null) {
      LOGGER.warn("$topic already exists - ignoring")
      return
    }
    set(topic)
    notifyChange(topic, Listener.Event.ADDED)
    LOGGER.info("topic $topic registered")
  }

  fun remove(topic: Topic) {
    getByTopic(topic)?.apply {
      unset(this)
      notifyChange(this, Listener.Event.REMOVED)
      LOGGER.info("topic $topic removed")
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

  companion object {
    private var LOGGER = Logger.getLogger(TopicRegistry::class.java)
  }
}
