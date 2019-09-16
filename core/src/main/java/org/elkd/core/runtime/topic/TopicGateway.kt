package org.elkd.core.runtime.topic

import org.elkd.core.client.consumer.Consumer
import org.elkd.core.client.producer.Producer

interface TopicGateway {

  fun consumersFor(topic: Topic): List<Consumer>

  fun producersFor(topic: Topic): List<Producer>

  fun registerConsumer(topic: Topic, consumer: Consumer)

  fun deregisterConsumer(topic: Topic, consumer: Consumer)

  fun registerProducer(topic: Topic, producer: Producer)

  fun deregisterProducer(topic: Topic, producer: Producer)

}
