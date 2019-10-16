package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.Dispatchers
import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.concurrency.asCoroutineScope
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.consensus.OpCategory
import org.elkd.core.runtime.NotificationsHub
import org.elkd.core.runtime.NotificationsHub.Channel
import org.elkd.core.runtime.topic.TopicModule
import org.elkd.core.runtime.client.producer.Producer
import org.elkd.core.server.client.RpcProducerAck
import org.elkd.core.server.client.RpcProducerRecord
import org.elkd.core.server.converters.KVConverters

class ClientStreamHandler(
    private val consensusFacade: ConsensusFacade,
    private val topicModule: TopicModule
) {

  private val producerRegistry: MutableSet<Producer> = mutableSetOf()

  init {
    NotificationsHub.sub(Channel.CONSENSUS_CHANGE, Dispatchers.Default) {
      // shutdown producers
      if (OpCategory.WRITE !in consensusFacade.supportedOperations) {
        producerRegistry.forEach(::shutdownProducer)
      }
    }
  }

  fun establishConsumerStream() { }

  fun establishProducerStream(responseObserver: StreamObserver<RpcProducerAck>): StreamObserver<RpcProducerRecord> {
    val producerVM = Producer(consensusFacade, Pools.createPool("producer").asCoroutineScope()).apply {
      registerProducer(this)
    }

    return StreamObserverDecorator(
        OnDispatcherStreamObserver(
        MetricStreamDecorator(
            ThrottlingStreamDecorator(
                ProducerRecordEnrichmentStreamDecorator(
                    producerVM.streamObserver(responseObserver),
                    responseObserver,
                    consensusFacade,
                    topicModule,
                    KVConverters.FromRpc()
                )
            )
        ),
        producerVM.coroutineScope
    ),
        onErrorBlock = { stream, t ->
          shutdownProducer(producerVM)
          stream.onError(t)
        },
        onCompleteBlock = {
          shutdownProducer(producerVM)
          it.onCompleted()
        }
    )
  }

  private fun shutdownProducer(producer: Producer) {
    LOGGER.info(">> de-registering $producer")
    producer.shutdown()
    producerRegistry.remove(producer)
  }

  private fun registerProducer(producer: Producer) {
    LOGGER.info(">> registering $producer")
    producerRegistry.add(producer)
  }

  companion object {
    private val LOGGER = Logger.getLogger(ClientStreamHandler::class.java)
  }
}
