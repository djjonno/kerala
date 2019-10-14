package org.elkd.core.runtime.client.producer

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.server.client.RpcProducerAck
import org.elkd.core.server.client.RpcProducerRecord
import java.util.concurrent.ExecutorService

private val LOGGER = Logger.getLogger(ProducerVM::class.java)

class ProducerVM(val threadPool: ExecutorService) {
  fun streamObserver(responseObserver: StreamObserver<RpcProducerAck>): StreamObserver<RpcProducerRecord> =
      object : StreamObserver<RpcProducerRecord> {
        override fun onNext(value: RpcProducerRecord) {
          LOGGER.info("process> $value")
          responseObserver.onNext(ProducerACK.Rpcs.OK)
        }

        override fun onError(t: Throwable) {
          responseObserver.onError(t)
        }

        override fun onCompleted() {
          responseObserver.onCompleted()
        }
      }
}
