package org.kerala.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.kerala.shared.client.ConsumerACK
import org.kerala.core.runtime.client.consumer.ConsumerRequest
import org.kerala.core.runtime.topic.TopicModule
import org.kerala.core.server.client.KeralaConsumerRequest
import org.kerala.core.server.client.KeralaConsumerResponse

class ConsumerEnrichmentStreamObserver(
    private val next: StreamObserver<ConsumerRequest>,
    private val back: StreamObserver<KeralaConsumerResponse>,
    private val topicModule: TopicModule
) : StreamObserver<KeralaConsumerRequest> {
  override fun onNext(value: KeralaConsumerRequest) {
    topicModule.topicRegistry.getByNamespace(value.topic)?.apply {
      /* Construct ConsumerRequest */
      next.onNext(ConsumerRequest(this, if (value.offset == -1L) null else value.offset))
    } ?: back.onNext(ConsumerACK.Rpcs.TOPIC_UNKNOWN)
  }

  override fun onError(t: Throwable?) {
    next.onError(t)
  }

  override fun onCompleted() {
    next.onCompleted()
  }
}
