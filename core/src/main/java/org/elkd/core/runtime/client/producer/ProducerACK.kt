package org.elkd.core.runtime.client.producer

import org.elkd.core.server.client.RpcProducerAck

object ProducerACK {

  object Codes {
    const val OK = 0
    const val CLIENT_ERROR = 1
    const val INVALID_OPERATION = 10
    const val UNKNOWN_TOPIC = 11
  }

  object Rpcs {
    val OK = RpcProducerAck.newBuilder().setNotification(Codes.OK).build()
    val CLIENT_ERROR = RpcProducerAck.newBuilder().setNotification(Codes.CLIENT_ERROR).build()
    val INVALID_OPERATION = RpcProducerAck.newBuilder().setNotification(Codes.INVALID_OPERATION).build()
    val UNKNOWN_TOPIC = RpcProducerAck.newBuilder().setNotification(Codes.UNKNOWN_TOPIC).build()
  }

}
