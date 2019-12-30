package org.kerala.ctl.commands.consumer

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.kerala.core.server.client.KeralaClientServiceGrpc
import org.kerala.core.server.client.KeralaConsumerRequest
import org.kerala.core.server.client.KeralaConsumerResponse
import org.kerala.shared.client.ConsumerACK

class StreamConsumer(stub: KeralaClientServiceGrpc.KeralaClientServiceStub) {

  val channel = Channel<KeralaConsumerResponse>()
  private val consumer = stub.keralaTopicConsumer(object : StreamObserver<KeralaConsumerResponse>, CoroutineScope by GlobalScope {
    override fun onNext(value: KeralaConsumerResponse) {
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
    consumer.onNext(KeralaConsumerRequest.newBuilder().setTopic(topic).setOffset(index).build())
  }
}
