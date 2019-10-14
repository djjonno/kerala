package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.elkd.core.concurrency.Pools
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.runtime.TopicModule
import org.elkd.core.runtime.client.producer.ProducerVM
import org.elkd.core.server.client.RpcProducerAck
import org.elkd.core.server.client.RpcProducerRecord
import org.elkd.core.server.converters.KVConverters

class ClientStreamHandler(
    private val consensusFacade: ConsensusFacade,
    private val topicModule: TopicModule
) {

  fun establishConsumerStream() { }

  fun establishProducerStream(responseObserver: StreamObserver<RpcProducerAck>): StreamObserver<RpcProducerRecord> {
    val producerVM = ProducerVM(consensusFacade, Pools.createPool("producer"))

    return MetricStreamDecorator(
        DispatchOnPoolStreamDecorator(
            ThrottlingStreamDecorator(
                ProducerRecordEnrichmentStreamDecorator(
                    producerVM.streamObserver(responseObserver),
                    responseObserver,
                    consensusFacade,
                    topicModule,
                    KVConverters.FromRpc()
                )
            ),
            producerVM.threadPool
        )
    )
  }
}
