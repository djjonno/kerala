package org.elkd.core.runtime

import org.elkd.core.runtime.topic.Topic
import org.elkd.core.runtime.topic.TopicGateway
import org.elkd.core.runtime.topic.TopicFactory
import org.elkd.core.runtime.topic.TopicRegistry

data class TopicModule(val topicRegistry: TopicRegistry,
                       val topicGateway: TopicGateway,
                       val topicFactory: TopicFactory) {
  val syslog: Topic = topicFactory.create("@syslog", "@syslog").also(topicRegistry::add)


  companion object {
    const val SYSLOG_NAMESPACE = "@syslog"
    const val SYSLOG_ID = "@syslog"
  }
}
