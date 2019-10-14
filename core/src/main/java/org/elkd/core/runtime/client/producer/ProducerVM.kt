package org.elkd.core.runtime.client.producer

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.server.client.RpcProducerAck
import org.elkd.core.server.client.RpcProducerRecord
import java.util.concurrent.ExecutorService

/**
 * ProducerVM
 *
 * Single-threaded pool for servicing production of a single Topic.
 */
class ProducerVM(
    val consensusFacade: ConsensusFacade,
    val threadPool: ExecutorService
) {
  fun streamObserver(responseObserver: StreamObserver<RpcProducerAck>): StreamObserver<ProducerRecord> =
      object : StreamObserver<ProducerRecord> {
        override fun onNext(value: ProducerRecord) {
          LOGGER.info(value.kvs)
          consensusFacade.writeToTopic(value.topic, value.kvs) {
            responseObserver.onNext(ProducerACK.Rpcs.OK)
          }
        }

        override fun onError(t: Throwable) {
          responseObserver.onError(t)
        }

        override fun onCompleted() {
          responseObserver.onCompleted()
        }
      }

  companion object {
    private val LOGGER = Logger.getLogger(ProducerVM::class.java)
  }
}
