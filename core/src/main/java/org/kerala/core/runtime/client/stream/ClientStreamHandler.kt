package org.kerala.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import java.io.Closeable
import kotlinx.coroutines.Dispatchers
import org.kerala.core.concurrency.Pools
import org.kerala.core.concurrency.asCoroutineScope
import org.kerala.core.consensus.ConsensusFacade
import org.kerala.core.consensus.OpCategory
import org.kerala.core.runtime.NotificationsHub
import org.kerala.core.runtime.NotificationsHub.Channel
import org.kerala.core.runtime.client.ack.ProducerACK
import org.kerala.core.runtime.client.consumer.Consumer
import org.kerala.core.runtime.client.producer.Producer
import org.kerala.core.runtime.topic.TopicModule
import org.kerala.core.server.client.RpcConsumerRequest
import org.kerala.core.server.client.RpcConsumerResponse
import org.kerala.core.server.client.RpcProducerRequest
import org.kerala.core.server.client.RpcProducerResponse
import org.kerala.core.server.converters.KVConverters

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
