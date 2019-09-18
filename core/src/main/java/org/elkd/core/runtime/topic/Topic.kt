package org.elkd.core.runtime.topic

/**
 * A topic represents a unique object which acts as the conduit between
 * a producer and a consumer.  The namespace property must be unique.
 */
data class Topic(
    val namespace: String
) {

  /**
   * Reserved Topic Namespaces
   */
  class Reserved {
    companion object {
      val SYSTEM_TOPIC = Builder("@system").build()
    }
  }

  class Builder(private val topicName: String) {
    fun build() : Topic {
      return Topic(topicName)
    }
  }
}
