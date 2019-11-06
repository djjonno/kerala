package org.kerala.core.runtime.client.consumer

import org.kerala.core.runtime.topic.Topic

data class ConsumerRequest(
    val topic: Topic,
    val index: Long?
)
