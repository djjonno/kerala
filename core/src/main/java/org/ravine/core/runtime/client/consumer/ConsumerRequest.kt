package org.ravine.core.runtime.client.consumer

import org.ravine.core.runtime.topic.Topic

data class ConsumerRequest(
    val topic: Topic,
    val index: Long?
)
