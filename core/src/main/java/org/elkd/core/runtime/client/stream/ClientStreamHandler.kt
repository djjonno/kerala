package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.Dispatchers
import org.elkd.core.concurrency.Pools
import org.elkd.core.concurrency.asCoroutineScope
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.consensus.OpCategory
import org.elkd.core.runtime.NotificationsHub
import org.elkd.core.runtime.NotificationsHub.Channel
import org.elkd.core.runtime.client.consumer.Consumer
import org.elkd.core.runtime.client.producer.Producer
import org.elkd.core.runtime.client.producer.ProducerACK
import org.elkd.core.runtime.topic.TopicModule
import org.elkd.core.server.client.RpcConsumerRequest
import org.elkd.core.server.client.RpcConsumerResponse
import org.elkd.core.server.client.RpcProducerAck
import org.elkd.core.server.client.RpcProducerRecord
import org.elkd.core.server.converters.KVConverters
import java.io.Closeable

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
                    ConsumerRequestEnrichmentStreamObserver(
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

  fun establishProducerStream(responseObserver: StreamObserver<RpcProducerAck>): StreamObserver<RpcProducerRecord> {
    val producer = Producer(consensusFacade, Pools.createPool("producer").asCoroutineScope()).apply {
      registerProducer(this)
    }

    return StreamObserverDecorator(
        OnDispatcherStreamObserver(
            MetricStreamDecorator(
                ThrottlingStreamDecorator(
                    ProducerRecordEnrichmentStreamDecorator(
                        producer.streamObserver(responseObserver),
                        responseObserver,
                        consensusFacade,
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
