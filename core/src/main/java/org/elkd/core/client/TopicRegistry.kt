package org.elkd.core.client

class TopicRegistry {
  fun register(topic: Topic) {

  }

  fun hasTopic(name: String): Boolean {
    return false
  }

  fun toList(): List<Topic> {
    return emptyList()
  }

  companion object {
    const val SYSTEM_TOPIC_NAME = "@system"
  }
}
