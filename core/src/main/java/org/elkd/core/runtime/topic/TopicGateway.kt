package org.elkd.core.runtime.topic

import org.elkd.core.concurrency.Pools
import org.elkd.core.runtime.NotificationCenter
import org.elkd.core.runtime.client.consumer.Consumer
import org.elkd.core.runtime.client.producer.ProducerVM

class TopicGateway {

  init {
    /**
     * Notify Producers when a consensus change occurs.  Producers may need to shutdown.
     */
    NotificationCenter.sub(NotificationCenter.Channel.CONSENSUS_CHANGE, Pools.createPool("topic-gateway")) { onConsensusChange() }
  }

  private val consumerRegistry: MutableMap<Topic, MutableSet<Consumer>> = mutableMapOf()

  private val producerVMRegistry: MutableMap<Topic, MutableSet<ProducerVM>> = mutableMapOf()
  fun consumersFor(topic: Topic): List<Consumer> = consumerRegistry[topic]?.toList() ?: emptyList()

  fun producersFor(topic: Topic): List<ProducerVM> = producerVMRegistry[topic]?.toList() ?: emptyList()

  fun registerConsumer(topic: Topic, consumer: Consumer) {
    if (!consumerRegistry.containsKey(topic)) {
      consumerRegistry[topic] = mutableSetOf()
    }

    consumerRegistry[topic]?.add(consumer)
  }

  fun registerProducer(topic: Topic, producerVM: ProducerVM) {
    if (!producerVMRegistry.containsKey(topic)) {
      producerVMRegistry[topic] = mutableSetOf()
    }

    producerVMRegistry[topic]?.add(producerVM)
  }

  fun deregisterConsumer(topic: Topic, consumer: Consumer) = consumerRegistry[topic]?.remove(consumer)

  fun deregisterProducer(topic: Topic, producerVM: ProducerVM) = producerVMRegistry[topic]?.remove(producerVM)

  private fun onConsensusChange() {

  }
}
