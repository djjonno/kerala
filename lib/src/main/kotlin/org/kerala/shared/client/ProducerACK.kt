package org.kerala.shared.client

import org.kerala.core.server.client.RpcProducerResponse

object ProducerACK {
  enum class Codes(val id: Int) {
    OK(0),
    SERVER_ERROR(1),
    OPERATION_INVALID(2),
    OPERATION_TIMEOUT(3),
    UNKNOWN_TOPIC(4)
  }

  object Rpcs {
    val OK: RpcProducerResponse = RpcProducerResponse.newBuilder().setStatus(Codes.OK.id).build()
    val SERVER_ERROR: RpcProducerResponse = RpcProducerResponse.newBuilder().setStatus(Codes.SERVER_ERROR.id).build()
    val OPERATION_INVALID: RpcProducerResponse = RpcProducerResponse.newBuilder().setStatus(Codes.OPERATION_INVALID.id).build()
    val OPERATION_TIMEOUT: RpcProducerResponse = RpcProducerResponse.newBuilder().setStatus(Codes.OPERATION_TIMEOUT.id).build()
    val UNKNOWN_TOPIC: RpcProducerResponse = RpcProducerResponse.newBuilder().setStatus(Codes.UNKNOWN_TOPIC.id).build()
  }
}
