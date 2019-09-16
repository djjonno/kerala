package org.elkd.core.runtime

import org.apache.log4j.Logger
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.runtime.SystemCommandType.CREATE_TOPIC
import org.elkd.core.runtime.SystemCommandType.LEADER_CHANGE
import org.elkd.core.runtime.client.ClientModule
import org.elkd.core.runtime.client.consumer.Consumer
import org.elkd.core.runtime.topic.Topic

class SystemConsumer(val clientModule: ClientModule) : Consumer {
  override fun consume(entry: Entry) {
    logger.info("consuming system entry -> $entry")

    val map = entry.kvs.map { it.key to it.`val` }.toMap()

    map["cmd"]?.apply {
      when (SystemCommandType.fromString(this)) {
        CREATE_TOPIC -> createTopic(map)
        LEADER_CHANGE -> { logger.info("leader changed.") }
      }
    }
  }

  private fun createTopic(kvs: Map<String, String>) {
    clientModule.topicRegistry.add(Topic(kvs["namespace"]!!))

    logger.info(clientModule.topicRegistry)
  }

  companion object {
    private var logger = Logger.getLogger(SystemConsumer::class.java)
  }
}
