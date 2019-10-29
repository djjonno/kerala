package org.elkd.core.runtime.client.consumer

import org.elkd.core.runtime.topic.Topic

data class ConsumerRequest(
    val topic: Topic,
    val index: Long?
)
