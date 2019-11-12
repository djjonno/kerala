package org.kerala.shared.client

import org.kerala.core.server.client.RpcConsumerResponse

object ConsumerACK {
  enum class Codes(val id: Int) {
    OK(0),
    OPERATION_INVALID(1),
    UNKNOWN_TOPIC(2)
  }

  object Rpcs {
    val UNKNOWN_TOPIC: RpcConsumerResponse = RpcConsumerResponse.newBuilder().setStatus(Codes.UNKNOWN_TOPIC.id).build()
    val OPERATION_INVALID: RpcConsumerResponse = RpcConsumerResponse.newBuilder().setStatus(Codes.OPERATION_INVALID.id).build()
  }
}
