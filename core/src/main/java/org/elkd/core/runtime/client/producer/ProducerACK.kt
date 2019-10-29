package org.elkd.core.runtime.client.producer

import org.elkd.core.server.client.RpcProducerAck

object ProducerACK {
  enum class Codes(val id: Int) {
    OK(0),
    CLIENT_ERROR(1),
    OPERATION_INVALID(2),
    OPERATION_TIMEOUT(3),
    UNKNOWN_TOPIC(4)
  }

  object Rpcs {
    val OK: RpcProducerAck = RpcProducerAck.newBuilder().setNotification(Codes.OK.id).build()
    val CLIENT_ERROR: RpcProducerAck = RpcProducerAck.newBuilder().setNotification(Codes.CLIENT_ERROR.id).build()
    val OPERATION_INVALID: RpcProducerAck = RpcProducerAck.newBuilder().setNotification(Codes.OPERATION_INVALID.id).build()
    val OPERATION_TIMEOUT: RpcProducerAck = RpcProducerAck.newBuilder().setNotification(Codes.OPERATION_TIMEOUT.id).build()
    val UNKNOWN_TOPIC: RpcProducerAck = RpcProducerAck.newBuilder().setNotification(Codes.UNKNOWN_TOPIC.id).build()
  }
}
