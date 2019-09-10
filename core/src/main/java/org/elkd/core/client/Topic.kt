package org.elkd.core.client

data class Topic(
    val name: String
) {
  companion object {
    inline fun builder(topicName: String,
                       topicBuilder: Builder.() -> Unit): Topic {
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
