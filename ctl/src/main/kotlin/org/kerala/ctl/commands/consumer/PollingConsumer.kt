package org.kerala.ctl.commands.consumer

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.kerala.core.server.client.ClientServiceGrpc
import org.kerala.core.server.client.RpcConsumerRequest
import org.kerala.core.server.client.RpcConsumerResponse

class PollingConsumer(stub: ClientServiceGrpc.ClientServiceStub) {

  val channel = Channel<RpcConsumerResponse>()
  private val consumer = stub.topicConsumer(object : StreamObserver<RpcConsumerResponse>, CoroutineScope by GlobalScope {
    override fun onNext(value: RpcConsumerResponse) {
      launch {
        channel.send(value)
      }
    }

    override fun onError(t: Throwable?) { }

    override fun onCompleted() { }
  })

  fun batch(topic: String, index: Long) {
    consumer.onNext(RpcConsumerRequest.newBuilder().setTopic(topic).setIndex(index).build())
  }
}
