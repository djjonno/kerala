package org.kerala.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.kerala.core.consensus.messages.KV
import org.kerala.core.runtime.client.producer.ProducerRecord
import org.kerala.core.runtime.topic.TopicModule
import org.kerala.core.server.client.KeralaKV
import org.kerala.core.server.client.KeralaProducerRequest
import org.kerala.core.server.client.KeralaProducerResponse
import org.kerala.core.server.converters.Converter
import org.kerala.shared.client.ProducerACK
import org.kerala.shared.logger

class ProducerEnrichmentStreamDecorator(
    private val next: StreamObserver<ProducerRecord>,
    private val responseObserver: StreamObserver<KeralaProducerResponse>,
    private val topicModule: TopicModule,
    private val kvConverter: Converter<KeralaKV, KV>
) : StreamObserver<KeralaProducerRequest> {

  override fun onNext(value: KeralaProducerRequest) {
    logger("enriching request")
    /* Check that topic exists - if so, create ProducerRecord and continue */
    topicModule.topicRegistry.getByNamespace(value.topic)?.apply {
      val record = ProducerRecord(this, value.kvsList.map(kvConverter::convert))
      next.onNext(record)
    } ?: responseObserver.onNext(ProducerACK.Rpcs.TOPIC_UNKNOWN)
  }

  override fun onError(t: Throwable) {
    next.onError(t)
  }

  override fun onCompleted() {
    next.onCompleted()
  }
}
