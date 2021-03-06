package org.kerala.core.runtime.client.consumer

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.kerala.core.consensus.ConsensusFacade
import org.kerala.core.server.client.KeralaConsumerResponse
import org.kerala.core.server.converters.KVConverters
import org.kerala.shared.client.ConsumerACK
import org.kerala.shared.logger
import java.io.Closeable

class Consumer(
    val consensusFacade: ConsensusFacade,
    val coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope, Closeable {

  private val kvConverter = KVConverters.ToRpc()

  override fun close() {
    coroutineContext.cancel()
  }

  fun streamObserver(responseObserver: StreamObserver<KeralaConsumerResponse>): StreamObserver<ConsumerRequest> =
      object : StreamObserver<ConsumerRequest> {
        override fun onNext(value: ConsumerRequest) {
          logger("consuming <- $value")

          /* @Note
           *
           * Consumer retrieval will only support single entry retrieval.
           * Multi-Entry retrieval will come later as there is some added
           * complexity in retrieving a window of entries from the log and
           * what the handling is if those indices don't yet exist.
           */
          runBlocking(coroutineContext) {
            try {
              val readResult = consensusFacade.readFromTopic(value.topic, value.offset)
              val kvs = readResult.entries
                  .map { it.kvs.map { kv ->
                      kvConverter.convert(kv)
                  } }
                  .flatten()
              responseObserver.onNext(KeralaConsumerResponse.newBuilder()
                  .setTopic(value.topic.namespace)
                  .setOffset(readResult.offset)
                  .addAllKvs(kvs)
                  .setResponseCode(ConsumerACK.Codes.OK.id)
                  .build())
            } catch (e: Exception) {
              responseObserver.onNext(KeralaConsumerResponse.newBuilder()
                  .setResponseCode(ConsumerACK.Codes.INVALID_OPERATION.id).build())
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
}
