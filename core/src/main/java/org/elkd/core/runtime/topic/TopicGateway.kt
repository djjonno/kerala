package org.elkd.core.runtime.topic

import org.elkd.core.runtime.client.consumer.Consumer
import org.elkd.core.runtime.client.producer.Producer

class TopicGateway {

  fun consumersFor(topic: Topic): List<Consumer> { return emptyList() }

  fun producersFor(topic: Topic): List<Producer> { return emptyList() }

  fun registerConsumer(topic: Topic, consumer: Consumer) { }

  fun deregisterConsumer(topic: Topic, consumer: Consumer) { }

  fun registerProducer(topic: Topic, producer: Producer) { }

  fun deregisterProducer(topic: Topic, producer: Producer) { }

}
