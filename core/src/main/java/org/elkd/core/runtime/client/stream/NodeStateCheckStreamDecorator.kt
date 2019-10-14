package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.consensus.OpCategory
import org.elkd.core.runtime.TopicModule
import org.elkd.core.runtime.client.producer.ProducerACK
import org.elkd.core.server.client.RpcProducerAck
import org.elkd.core.server.client.RpcProducerRecord

class NodeStateCheckStreamDecorator(private val next: StreamObserver<RpcProducerRecord>,
                                    private val responseObserver: StreamObserver<RpcProducerAck>,
                                    private val consensusFacade: ConsensusFacade,
                                    private val topicModule: TopicModule) : StreamObserver<RpcProducerRecord> {

  override fun onNext(value: RpcProducerRecord) {
    LOGGER.info("checking node ops ${consensusFacade.supportedOperations}")
    if (OpCategory.WRITE in consensusFacade.supportedOperations) {
      LOGGER.info("checking topic ${value.topic}")
      topicModule.topicRegistry.getByNamespace(value.topic)?.apply {
        next.onNext(value)
      } ?: responseObserver.onNext(ProducerACK.Rpcs.UNKNOWN_TOPIC)
    } else {
      responseObserver.onNext(ProducerACK.Rpcs.INVALID_OPERATION)
    }
  }

  override fun onError(t: Throwable) {
    next.onError(t)
  }

  override fun onCompleted() {
    next.onCompleted()
  }

  companion object {
    private val LOGGER = Logger.getLogger(NodeStateCheckStreamDecorator::class.java)
  }
}
