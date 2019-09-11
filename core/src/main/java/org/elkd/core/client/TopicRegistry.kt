package org.elkd.core.client

import org.apache.log4j.Logger

class TopicRegistry {
  private val registry: MutableMap<String, Topic> = mutableMapOf()

  fun add(topic: Topic) {
    if (hasTopic(topic)) {
      log.warn("$topic already exists, ignoring op.")
      return
    }
    registry[topic.namespace] = topic
    log.info("new topic registered: $topic")
  }

  fun remove(topic: Topic) {
    if (!hasTopic(topic)) {
      log.warn("$topic does not exist, ignoring op.")
      return
    }
    registry.remove(topic.namespace)
    log.info("topic removed: $topic")
  }

  fun hasTopic(name: String) = registry.containsKey(name)
  private fun hasTopic(topic: Topic) = hasTopic(topic.namespace)

  fun toList(): List<Topic> {
    return registry.entries.map { it.value }
  }

  override fun toString(): String {
    return "TopicRegistry($registry)"
  }

  companion object {
    private var log = Logger.getLogger(TopicRegistry::class.java)
  }
}
