package org.ravine.core.runtime.client.consumer

import io.grpc.stub.StreamObserver
import java.io.Closeable
import java.lang.Exception
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.apache.log4j.Logger
import org.ravine.core.consensus.ConsensusFacade
import org.ravine.core.runtime.client.ack.ConsumerACK
import org.ravine.core.server.client.RpcConsumerResponse
import org.ravine.core.server.converters.KVConverters

class Consumer(
    val consensusFacade: ConsensusFacade,
    val coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope, Closeable {

  private val kvConverter = KVConverters.ToRpc()

  override fun close() {
    coroutineContext.cancel()
  }

  fun streamObserver(responseObserver: StreamObserver<RpcConsumerResponse>): StreamObserver<ConsumerRequest> =
      object : StreamObserver<ConsumerRequest> {
        override fun onNext(value: ConsumerRequest) {
          LOGGER.info("consuming <- $value")

          /* @Note
           *
           * Consumer retrieval will only support single entry retrieval.
           * Multi-Entry retrieval will come later as there is some added
           * complexity in retrieving a window of entries from the log and
           * what the handling is if those indices don't yet exist.
           */
          runBlocking(coroutineContext) {
            try {
              val kvs = consensusFacade.readFromTopic(value.topic, value.index)
                  .map {
                    it.kvs.map { kv ->
                      kvConverter.convert(kv)
                    }
                  }
                  .flatten()
              responseObserver.onNext(RpcConsumerResponse.newBuilder()
                  .addAllKvs(kvs)
                  .setStatus(ConsumerACK.Codes.OK.id).build())
            } catch (e: Exception) {
              responseObserver.onNext(RpcConsumerResponse.newBuilder()
                  .setStatus(ConsumerACK.Codes.OPERATION_INVALID.id).build())
            }
          }
        }

        override fun onError(t: Throwable?) {
          responseObserver.onError(t)
        }

        override fun onCompleted() {
          responseObserver.onCompleted()
        }
      }

  companion object {
    private val LOGGER = Logger.getLogger(Consumer::class.java)
  }
}
