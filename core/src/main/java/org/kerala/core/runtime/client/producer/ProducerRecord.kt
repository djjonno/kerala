package org.kerala.core.runtime.client.producer

import org.kerala.core.consensus.messages.KV
import org.kerala.core.runtime.topic.Topic

data class ProducerRecord(val topic: Topic, val kvs: List<KV>)
