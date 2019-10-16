package org.elkd.core.runtime.topic

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.LogChangeListener
import org.elkd.core.runtime.client.consumer.SyslogConsumer

class TopicModule(
    val topicRegistry: TopicRegistry,
    val consumerGateway: ConsumerGateway,
    private val topicFactory: TopicFactory
) {

  val syslog: Topic = bootstrapSyslog()

  private fun bootstrapSyslog(): Topic {
    val topic = provisionTopic(SYSLOG_ID, SYSLOG_NAMESPACE)

    consumerGateway.registerConsumer(topic, SyslogConsumer(this))

    topic.logFacade.registerListener(object : LogChangeListener<Entry> {
      override fun onCommit(index: Long, entry: Entry) {
        consumerGateway.consumersFor(topic).forEach {
          it.consume(index, entry)
        }
      }
    })

    return topic
  }

  fun provisionTopic(id: String, namespace: String): Topic {
    return topicFactory.create(id, namespace).also(topicRegistry::add)
  }

  companion object {
    const val SYSLOG_NAMESPACE = "@syslog"
    const val SYSLOG_ID = "@syslog"
  }
}
