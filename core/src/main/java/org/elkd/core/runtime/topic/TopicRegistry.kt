package org.elkd.core.runtime.topic

import org.apache.log4j.Logger

class TopicRegistry {
  private val registry: MutableMap<String, Topic> = mutableMapOf()

  fun add(topic: Topic) {
    if (get(topic) != null) {
      logger.warn("$topic already exists, ignoring op.")
      return
    }
    registry[topic.namespace] = topic
    logger.info("new topic registered: $topic")
  }

  fun remove(topic: Topic) {
    get(topic)?.apply {
      registry.remove(this.namespace)
      logger.info("topic removed: $topic")
    }
  }

  fun get(name: String): Topic? = registry.get(name)
  fun get(topic: Topic): Topic? = get(topic.namespace)

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
