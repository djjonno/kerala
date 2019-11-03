package org.ravine.core.runtime.client.producer

import org.ravine.core.consensus.messages.KV
import org.ravine.core.runtime.topic.Topic

data class ProducerRecord(val topic: Topic, val kvs: List<KV>)
