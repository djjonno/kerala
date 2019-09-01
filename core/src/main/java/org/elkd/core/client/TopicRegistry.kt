package org.elkd.core.client

interface TopicRegistry {

  fun register(topic: Topic)

  fun find(name: String): Topic?

}
