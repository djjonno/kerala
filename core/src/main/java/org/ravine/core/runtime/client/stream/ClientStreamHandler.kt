package org.ravine.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import java.io.Closeable
import kotlinx.coroutines.Dispatchers
import org.ravine.core.concurrency.Pools
import org.ravine.core.concurrency.asCoroutineScope
import org.ravine.core.consensus.ConsensusFacade
import org.ravine.core.consensus.OpCategory
import org.ravine.core.runtime.NotificationsHub
import org.ravine.core.runtime.NotificationsHub.Channel
import org.ravine.core.runtime.client.ack.ProducerACK
import org.ravine.core.runtime.client.consumer.Consumer
import org.ravine.core.runtime.client.producer.Producer
import org.ravine.core.runtime.topic.TopicModule
import org.ravine.core.server.client.RpcConsumerRequest
import org.ravine.core.server.client.RpcConsumerResponse
import org.ravine.core.server.client.RpcProducerRequest
import org.ravine.core.server.client.RpcProducerResponse
import org.ravine.core.server.converters.KVConverters

class ClientStreamHandler(
    private val consensusFacade: ConsensusFacade,
    private val topicModule: TopicModule
) {

  private val producerRegistry: MutableSet<Closeable> = mutableSetOf()

  init {
    NotificationsHub.sub(Channel.CONSENSUS_CHANGE, Dispatchers.Default) {
      // shutdown producers
      if (!consensusFacade.supportsCategory(OpCategory.WRITE)) {
        producerRegistry.forEach(::shutdownProducer)
      }
    }
  }

  fun establishConsumerStream(responseObserver: StreamObserver<RpcConsumerResponse>): StreamObserver<RpcConsumerRequest> {
    val consumer = Consumer(consensusFacade, Pools.createPool("consumer").asCoroutineScope())

    return StreamObserverDecorator(
        OnDispatcherStreamObserver(
            MetricStreamDecorator(
                ThrottlingStreamDecorator(
                    ConsumerEnrichmentStreamObserver(
                        consumer.streamObserver(responseObserver),
                        responseObserver,
                        topicModule
                    )
                )
            ),
            consumer.coroutineScope
        ),
        onErrorBlock = { stream, t ->
          stream.onError(t)
          consumer.close()
        },
        onCompleteBlock = {
          it.onCompleted()
          consumer.close()
        }
    )
  }

  fun establishProducerStream(responseObserver: StreamObserver<RpcProducerResponse>): StreamObserver<RpcProducerRequest> {
    val producer = Producer(consensusFacade, Pools.createPool("producer").asCoroutineScope()).apply {
      registerProducer(this)
    }

    return StreamObserverDecorator(
        OnDispatcherStreamObserver(
            MetricStreamDecorator(
                ThrottlingStreamDecorator(
                    ProducerEnrichmentStreamDecorator(
                        producer.streamObserver(responseObserver),
                        responseObserver,
                        topicModule,
                        KVConverters.FromRpc()
                    )
                )
            ),
            producer.coroutineScope
        ),
        onNextBlock = { stream, value ->
          /* Verify node state supports WRITES */
          if (consensusFacade.supportsCategory(OpCategory.WRITE)) {
            stream.onNext(value)
          } else {
            responseObserver.onNext(ProducerACK.Rpcs.OPERATION_INVALID)
          }
        },
        onErrorBlock = { stream, t ->
          shutdownProducer(producer)
          stream.onError(t)
        },
        onCompleteBlock = {
          shutdownProducer(producer)
          it.onCompleted()
        }
    )
  }

  private fun shutdownProducer(closeable: Closeable) {
    closeable.close()
    producerRegistry.remove(closeable)
  }

  private fun registerProducer(producer: Producer) {
    producerRegistry.add(producer)
  }
}
