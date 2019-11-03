package org.ravine.core.runtime.topic

import org.ravine.core.consensus.messages.Entry
import org.ravine.core.log.LogChangeListener
import org.ravine.core.runtime.client.consumer.SyslogConsumer

class TopicModule(
    val topicRegistry: TopicRegistry,
    private val topicFactory: TopicFactory
) {

  val syslog: Topic = bootstrapSyslog()

  private fun bootstrapSyslog(): Topic {
    val topic = provisionTopic(SYSLOG_ID, SYSLOG_NAMESPACE)

    val syslogConsumer = SyslogConsumer(this)
    topic.logFacade.registerListener(object : LogChangeListener<Entry> {
      override fun onCommit(index: Long, entry: Entry) {
        syslogConsumer.consume(entry)
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
