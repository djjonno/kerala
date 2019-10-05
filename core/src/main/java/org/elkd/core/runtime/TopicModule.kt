package org.elkd.core.runtime

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.LogChangeListener
import org.elkd.core.runtime.client.consumer.SyslogConsumer
import org.elkd.core.runtime.topic.Topic
import org.elkd.core.runtime.topic.TopicFactory
import org.elkd.core.runtime.topic.TopicGateway
import org.elkd.core.runtime.topic.TopicRegistry

class TopicModule(val topicRegistry: TopicRegistry,
                  val topicGateway: TopicGateway,
                  private val topicFactory: TopicFactory) {

  val syslog: Topic = bootstrapSyslog()

  private fun bootstrapSyslog() : Topic {
    val topic = provisionTopic(SYSLOG_ID, SYSLOG_NAMESPACE)

    topicGateway.registerConsumer(topic, SyslogConsumer(this))

    topic.logFacade.registerListener(object : LogChangeListener<Entry> {
      override fun onCommit(index: Long, entry: Entry) {
        topicGateway.consumersFor(topic).forEach {
          it.consume(index, entry)
        }
      }
    })

    return topic
  }

  fun provisionTopic(id: String, namespace: String) : Topic {
    return topicFactory.create(id, namespace).also(topicRegistry::add)
  }

  companion object {
    const val SYSLOG_NAMESPACE = "@syslog"
    const val SYSLOG_ID = "@syslog"
  }
}
