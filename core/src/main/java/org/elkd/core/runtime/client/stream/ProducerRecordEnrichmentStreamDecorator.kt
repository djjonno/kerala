package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.consensus.OpCategory
import org.elkd.core.consensus.messages.KV
import org.elkd.core.runtime.topic.TopicModule
import org.elkd.core.runtime.client.producer.ProducerACK
import org.elkd.core.runtime.client.producer.ProducerRecord
import org.elkd.core.server.client.RpcKV
import org.elkd.core.server.client.RpcProducerAck
import org.elkd.core.server.client.RpcProducerRecord
import org.elkd.core.server.converters.Converter

class ProducerRecordEnrichmentStreamDecorator(
    private val next: StreamObserver<ProducerRecord>,
    private val responseObserver: StreamObserver<RpcProducerAck>,
    private val consensusFacade: ConsensusFacade,
    private val topicModule: TopicModule,
    private val kvConverter: Converter<RpcKV, KV>
) : StreamObserver<RpcProducerRecord> {

  override fun onNext(value: RpcProducerRecord) {
    LOGGER.info("enriching RpcProducerRecord")
    /* Check node can support a production */
    if (OpCategory.WRITE in consensusFacade.supportedOperations) {

      /* Check that topic exists - if so, create ProducerRecord and continue */
      topicModule.topicRegistry.getByNamespace(value.topic)?.apply {
        val record = ProducerRecord(this, value.kvList.map(kvConverter::convert))
        next.onNext(record)
      } ?: responseObserver.onNext(ProducerACK.Rpcs.UNKNOWN_TOPIC)

    } else {
      responseObserver.onNext(ProducerACK.Rpcs.OPERATION_INVALID)
    }
  }

  override fun onError(t: Throwable) {
    next.onError(t)
  }

  override fun onCompleted() {
    next.onCompleted()
  }

  companion object {
    private val LOGGER = Logger.getLogger(ProducerRecordEnrichmentStreamDecorator::class.java)
  }
}
