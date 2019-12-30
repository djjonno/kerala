package org.kerala.ctl.commands.producer

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.kerala.core.server.client.KeralaClientServiceGrpc
import org.kerala.core.server.client.KeralaKV
import org.kerala.core.server.client.KeralaProducerRequest
import org.kerala.core.server.client.KeralaProducerResponse
import org.kerala.shared.client.ProducerACK

class StreamProducer(stub: KeralaClientServiceGrpc.KeralaClientServiceStub) {

  private val channel = Channel<KeralaProducerResponse>()
  private val produce = stub.keralaTopicProducer(object : StreamObserver<KeralaProducerResponse>, CoroutineScope by GlobalScope {
    override fun onNext(value: KeralaProducerResponse) {
      launch {
        channel.send(value)
      }
    }

    override fun onError(t: Throwable) {
      launch {
        /* As per client producer contract, the onError channel is reserved for
         * IO issues hence why we bubble this up as a NETWORK_ERROR.
         * */
        channel.send(ProducerACK.Rpcs.NETWORK_ERROR)
      }
    }

    override fun onCompleted() {
      println("stream closed")
    }
  })

  suspend fun batch(topic: String, kvs: List<KeralaKV>): KeralaProducerResponse {
    produce.onNext(KeralaProducerRequest
        .newBuilder()
        .setTopic(topic)
        .addAllKvs(kvs)
        .build())
    return channel.receive()
  }

  fun complete() {
    produce.onCompleted()
  }
}
