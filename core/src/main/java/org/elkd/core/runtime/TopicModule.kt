package org.elkd.core.runtime

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.LogChangeListener
import org.elkd.core.runtime.client.consumer.SystemConsumer
import org.elkd.core.runtime.topic.Topic
import org.elkd.core.runtime.topic.TopicGateway
import org.elkd.core.runtime.topic.TopicFactory
import org.elkd.core.runtime.topic.TopicRegistry

data class TopicModule(val topicRegistry: TopicRegistry,
                       val topicGateway: TopicGateway,
                       val topicFactory: TopicFactory) {

  val syslog: Topic

  init {
    syslog = bootstrapSysLog()
  }

  private fun bootstrapSysLog() : Topic {
    val topic = topicFactory.create(SYSLOG_NAMESPACE, SYSLOG_ID).also(topicRegistry::add)

    topicGateway.registerConsumer(topic, SystemConsumer(this))

    // Temp
    topic.logFacade.registerListener(object : LogChangeListener<Entry> {
      override fun onCommit(index: Long, entry: Entry) {
        topicGateway.consumersFor(topic).forEach {
          it.consume(index, entry)
        }
      }
    })

    return topic
  }

  fun provisionNewTopic(id: String, namespace: String) : Topic {
    return topicFactory.create(id, namespace).also(topicRegistry::add)
  }

  companion object {
    const val SYSLOG_NAMESPACE = "@syslog"
    const val SYSLOG_ID = "@syslog"
  }
}
