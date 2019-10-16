package org.elkd.core.runtime.client.producer

import org.elkd.core.server.client.RpcProducerAck

object ProducerACK {

  object Codes {
    const val OK = 0
    const val CLIENT_ERROR = 1
    const val OPERATION_INVALID = 10
    const val OPERATION_TIMEOUT = 11
    const val UNKNOWN_TOPIC = 11
  }

  object Rpcs {
    val OK: RpcProducerAck = RpcProducerAck.newBuilder().setNotification(Codes.OK).build()
    val CLIENT_ERROR: RpcProducerAck = RpcProducerAck.newBuilder().setNotification(Codes.CLIENT_ERROR).build()
    val OPERATION_INVALID: RpcProducerAck = RpcProducerAck.newBuilder().setNotification(Codes.OPERATION_INVALID).build()
    val OPERATION_TIMEOUT: RpcProducerAck = RpcProducerAck.newBuilder().setNotification(Codes.OPERATION_TIMEOUT).build()
    val UNKNOWN_TOPIC: RpcProducerAck = RpcProducerAck.newBuilder().setNotification(Codes.UNKNOWN_TOPIC).build()
  }
}
