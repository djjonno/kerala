package org.kerala.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.kerala.core.consensus.messages.KV
import org.kerala.shared.client.ProducerACK
import org.kerala.core.runtime.client.producer.ProducerRecord
import org.kerala.core.runtime.topic.TopicModule
import org.kerala.core.server.client.RpcKV
import org.kerala.core.server.client.RpcProducerRequest
import org.kerala.core.server.client.RpcProducerResponse
import org.kerala.core.server.converters.Converter

class ProducerEnrichmentStreamDecorator(
    private val next: StreamObserver<ProducerRecord>,
    private val responseObserver: StreamObserver<RpcProducerResponse>,
    private val topicModule: TopicModule,
    private val kvConverter: Converter<RpcKV, KV>
) : StreamObserver<RpcProducerRequest> {

  override fun onNext(value: RpcProducerRequest) {
    LOGGER.info("enriching request")
    /* Check that topic exists - if so, create ProducerRecord and continue */
    topicModule.topicRegistry.getByNamespace(value.topic)?.apply {
      val record = ProducerRecord(this, value.kvsList.map(kvConverter::convert))
      next.onNext(record)
    } ?: responseObserver.onNext(ProducerACK.Rpcs.UNKNOWN_TOPIC)
  }

  override fun onError(t: Throwable) {
    next.onError(t)
  }

  override fun onCompleted() {
    next.onCompleted()
  }

  companion object {
    private val LOGGER = Logger.getLogger(ProducerEnrichmentStreamDecorator::class.java)
  }
}
