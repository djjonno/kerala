package org.elkd.core.runtime.topic

import org.elkd.core.runtime.client.consumer.Consumer

class ConsumerGateway {

  private val consumerRegistry: MutableMap<Topic, MutableSet<Consumer>> = mutableMapOf()

  fun consumersFor(topic: Topic): List<Consumer> = consumerRegistry[topic]?.toList() ?: emptyList()

  fun registerConsumer(topic: Topic, consumer: Consumer) {
    consumerRegistry[topic] = consumerRegistry.getOrDefault(topic, mutableSetOf()).apply {
      add(consumer)
    }
  }

  fun deregisterConsumer(topic: Topic, consumer: Consumer) = consumerRegistry[topic]?.remove(consumer)

}
