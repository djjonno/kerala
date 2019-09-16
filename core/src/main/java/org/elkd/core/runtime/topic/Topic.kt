package org.elkd.core.runtime.topic

/**
 * A topic represents a unique object which acts as the conduit between
 * a producer and a consumer.  The namespace property must be unique.
 */
data class Topic(
    val namespace: String
) {
  companion object {

    /* --------------------- Reserved Topic NameSpaces --------------------- */

    const val SYSTEM_TOPIC_NAMESPACE = "@system"
    val SYSTEM_TOPIC = builder(SYSTEM_TOPIC_NAMESPACE)

    /* --------------------------------------------------------------------- */

    inline fun builder(topicName: String,
                       topicBuilder: Builder.() -> Unit = {}): Topic {
      val builder = Builder(topicName)
      builder.topicBuilder()
      return builder.build()
    }
  }

  class Builder(private val topicName: String) {
    fun build() : Topic {
      return Topic(topicName)
    }
  }
}
