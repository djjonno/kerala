package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.runtime.TopicModule
import org.elkd.core.runtime.client.producer.ProducerVM
import org.elkd.core.server.client.RpcProducerAck
import org.elkd.core.server.client.RpcProducerRecord

private val LOGGER = Logger.getLogger(ClientStreamHandler::class.java)

class ClientStreamHandler(
    val consensusFacade: ConsensusFacade,
    val topicModule: TopicModule
) {

  fun establishConsumerStream() { }

  fun establishProducerStream(responseObserver: StreamObserver<RpcProducerAck>): StreamObserver<RpcProducerRecord> {
    val pool = Pools.createPool("producer")
    val producerVM = ProducerVM(pool)

    return MetricStreamDecorator(
        DispatchOnPoolStreamDecorator(
            ThrottlingStreamDecorator(
                NodeStateCheckStreamDecorator(
                    producerVM.streamObserver(responseObserver),
                    responseObserver,
                    consensusFacade,
                    topicModule
                ),
                10
            ),
            pool
        )
    )
  }
}
