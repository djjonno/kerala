package org.kerala.ctl.commands.consumer

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.kerala.core.server.client.ClientServiceGrpc
import org.kerala.core.server.client.RpcConsumerRequest
import org.kerala.core.server.client.RpcConsumerResponse
import org.kerala.shared.client.ConsumerACK

class StreamConsumer(stub: ClientServiceGrpc.ClientServiceStub) {

  val channel = Channel<RpcConsumerResponse>()
  private val consumer = stub.topicConsumer(object : StreamObserver<RpcConsumerResponse>, CoroutineScope by GlobalScope {
    override fun onNext(value: RpcConsumerResponse) {
      launch {
        channel.send(value)
      }
    }

    override fun onError(t: Throwable?) {
      launch {
        /* As per client producer contract, the onError channel is reserved for
         * IO issues hence why we bubble this up as a NETWORK_ERROR.
         * */
        channel.send(ConsumerACK.Rpcs.NETWORK_ERROR)
      }
    }

    override fun onCompleted() {
      println("stream closed")
    }
  })

  fun batch(topic: String, index: Long) {
    consumer.onNext(RpcConsumerRequest.newBuilder().setTopic(topic).setIndex(index).build())
  }
}
