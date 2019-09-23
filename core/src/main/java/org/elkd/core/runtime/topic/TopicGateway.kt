package org.elkd.core.runtime.topic

import org.elkd.core.runtime.client.consumer.Consumer
import org.elkd.core.runtime.client.producer.Producer

class TopicGateway {

  private val consumerRegistry: MutableMap<Topic, MutableSet<Consumer>> = mutableMapOf()
  private val producerRegistry: MutableMap<Topic, MutableSet<Producer>> = mutableMapOf()

  fun consumersFor(topic: Topic): List<Consumer> = consumerRegistry[topic]?.toList() ?: emptyList()

  fun producersFor(topic: Topic): List<Producer> = producerRegistry[topic]?.toList() ?: emptyList()

  fun registerConsumer(topic: Topic, consumer: Consumer) {
    if (!consumerRegistry.containsKey(topic)) {
      consumerRegistry[topic] = mutableSetOf()
    }

    consumerRegistry[topic]?.add(consumer)
  }

  fun registerProducer(topic: Topic, producer: Producer) {
    if (!producerRegistry.containsKey(topic)) {
      producerRegistry[topic] = mutableSetOf()
    }

    producerRegistry[topic]?.add(producer)
  }

  fun deregisterConsumer(topic: Topic, consumer: Consumer) = consumerRegistry[topic]?.remove(consumer)

  fun deregisterProducer(topic: Topic, producer: Producer) = producerRegistry[topic]?.remove(producer)

}
