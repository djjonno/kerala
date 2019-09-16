package org.elkd.core.runtime.topic

import org.elkd.core.runtime.client.consumer.Consumer
import org.elkd.core.runtime.client.producer.Producer

class TopicGateway {

  private val consumerRegistry: MutableMap<Topic, MutableSet<Consumer>> = mutableMapOf()
  private val producerRegistry: MutableMap<Topic, Producer> = mutableMapOf()

  fun consumersFor(topic: Topic): List<Consumer> = consumerRegistry[topic]?.toList() ?: emptyList()

  fun producerFor(topic: Topic): Producer? { return null }

  fun registerConsumer(topic: Topic, consumer: Consumer) {
    if (!consumerRegistry.containsKey(topic)) {
      consumerRegistry[topic] = mutableSetOf()
    }

    consumerRegistry[topic]?.add(consumer)
  }

  fun deregisterConsumer(topic: Topic, consumer: Consumer) { }

  fun registerProducer(topic: Topic, producer: Producer) { }

  fun deregisterProducer(topic: Topic, producer: Producer) { }

}
