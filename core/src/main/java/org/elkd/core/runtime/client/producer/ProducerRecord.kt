package org.elkd.core.runtime.client.producer

import org.elkd.core.consensus.messages.KV
import org.elkd.core.runtime.topic.Topic

data class ProducerRecord(val topic: Topic, val kvs: List<KV>)
