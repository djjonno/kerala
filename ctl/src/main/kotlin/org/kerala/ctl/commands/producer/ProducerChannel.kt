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

class ProducerChannel(stub: ClientServiceGrpc.ClientServiceStub) {

  private val channel = Channel<Int>()
  private val produce = stub.topicProducer(object : StreamObserver<RpcProducerResponse>, CoroutineScope by GlobalScope {
    override fun onNext(value: RpcProducerResponse) {
      launch {
        channel.send(value.status)
      }
    }

    override fun onError(t: Throwable) {
      println(t)
    }

    override fun onCompleted() {
      println("stream completed")
    }
  })

  suspend fun batch(topic: String, kvs: List<RpcKV>): Int {
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
