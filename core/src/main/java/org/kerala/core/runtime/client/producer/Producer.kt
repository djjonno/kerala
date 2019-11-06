package org.kerala.core.runtime.client.producer

import io.grpc.stub.StreamObserver
import java.io.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.apache.log4j.Logger
import org.kerala.core.consensus.ConsensusFacade
import org.kerala.core.log.exceptions.Event
import org.kerala.core.log.exceptions.LogChangeException
import org.kerala.core.runtime.client.ack.ProducerACK
import org.kerala.core.server.client.RpcProducerResponse
import java.lang.Exception
import java.time.Duration

/**
 * Producer
 *
 * Receives productions from a client, dispatches to Topic, and responds
 * outcome to client.  The Producer manages a single client, on a single
 * thread.
 *
 * A Producer can be shutdown, and the connected client will be notified
 * with an appropriate response.
 */
class Producer(
    val consensusFacade: ConsensusFacade,
    val coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope, Closeable {

  override fun close() {
    LOGGER.info("shutting down producer vm")
    coroutineContext.cancel()
  }

  fun streamObserver(responseObserver: StreamObserver<RpcProducerResponse>): StreamObserver<ProducerRecord> =
      object : StreamObserver<ProducerRecord> {
        override fun onNext(value: ProducerRecord) {
          LOGGER.info("ingesting ${value.kvs.size} KVs -> ${value.topic}")

          /* Dispatching record to Topic is essentially guaranteed,
           * provided:
           * 1) this node is leader
           * 2) this node is not voted out
           *
           * #1 is addressed by Stream Enrichment which occurs prior
           * to the Producer receiving the message.
           * #2 is addressed by listening for consensus changes,
           * and shutting down producers if node no longer
           * supports WRITE ops.
          */
          runBlocking(coroutineContext) {
            try {
              consensusFacade.writeToTopic(value.topic, value.kvs, Duration.ofSeconds(1))
              LOGGER.info("committed ✓")
              responseObserver.onNext(ProducerACK.Rpcs.OK)
            } catch (e: LogChangeException) {
              responseObserver.onNext(when (e.event) {
                Event.TIMEOUT -> ProducerACK.Rpcs.OPERATION_TIMEOUT
              })
            } catch (e: Exception) {
              responseObserver.onNext(ProducerACK.Rpcs.SERVER_ERROR)
            }
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
    private val LOGGER = Logger.getLogger(Producer::class.java)
  }
}
