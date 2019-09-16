package org.elkd.core.runtime.topic

import org.apache.log4j.Logger

class TopicRegistry {
  private val registry: MutableMap<String, Topic> = mutableMapOf()

  fun add(topic: Topic) {
    if (hasTopic(topic)) {
      logger.warn("$topic already exists, ignoring op.")
      return
    }
    registry[topic.namespace] = topic
    logger.info("new topic registered: $topic")
  }

  fun remove(topic: Topic) {
    if (!hasTopic(topic)) {
      logger.warn("$topic does not exist, ignoring op.")
      return
    }
    registry.remove(topic.namespace)
    logger.info("topic removed: $topic")
  }

  fun hasTopic(name: String) = registry.containsKey(name)
  fun hasTopic(topic: Topic) = hasTopic(topic.namespace)

  fun toList(): List<Topic> {
    return registry.entries.map { it.value }
  }

  override fun toString(): String {
    return "TopicRegistry($registry)"
  }

  companion object {
    private var logger = Logger.getLogger(TopicRegistry::class.java)
  }
}
