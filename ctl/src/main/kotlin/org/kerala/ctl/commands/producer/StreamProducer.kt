package org.kerala.ctl.commands.producer

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.kerala.core.server.client.ClientServiceGrpc
import org.kerala.core.server.client.RpcKV
import org.kerala.core.server.client.RpcProducerRequest
import org.kerala.core.server.client.RpcProducerResponse
import org.kerala.shared.client.ProducerACK

class StreamProducer(stub: ClientServiceGrpc.ClientServiceStub) {

  private val channel = Channel<RpcProducerResponse>()
  private val produce = stub.topicProducer(object : StreamObserver<RpcProducerResponse>, CoroutineScope by GlobalScope {
    override fun onNext(value: RpcProducerResponse) {
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

  suspend fun batch(topic: String, kvs: List<RpcKV>): RpcProducerResponse {
    produce.onNext(RpcProducerRequest
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
