package org.elkd.core.runtime

import org.elkd.core.runtime.topic.TopicGateway
import org.elkd.core.runtime.topic.TopicFactory
import org.elkd.core.runtime.topic.TopicRegistry

data class RuntimeModule(val topicRegistry: TopicRegistry,
                         val topicGateway: TopicGateway,
                         val topicFactory: TopicFactory)
