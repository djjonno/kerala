package org.elkd.core.client

class TopicRegistry {

  companion object {
    const val SYSTEM_TOPIC = "@system"
  }

  fun register(topic: Topic) {

  }

  fun hasTopic(name: String): Boolean {
    return false
  }

  fun toList(): List<Topic> {
    return emptyList()
  }
}
