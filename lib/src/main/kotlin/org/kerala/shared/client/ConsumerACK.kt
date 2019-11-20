package org.kerala.shared.client

import org.kerala.core.server.client.RpcConsumerResponse

object ConsumerACK {
  enum class Codes(val id: Int) {
    OK(0),
    GENERIC_ERROR(1),
    NETWORK_ERROR(2),
    INVALID_OPERATION(3),
    TOPIC_UNKNOWN(4)
  }

  object Rpcs {
    val OK: RpcConsumerResponse = RpcConsumerResponse.newBuilder().setStatus(Codes.OK.id).build()
    val GENERIC_ERROR: RpcConsumerResponse = RpcConsumerResponse.newBuilder().setStatus(Codes.GENERIC_ERROR.id).build()
    val NETWORK_ERROR: RpcConsumerResponse = RpcConsumerResponse.newBuilder().setStatus(Codes.NETWORK_ERROR.id).build()
    val INVALID_OPERATION: RpcConsumerResponse = RpcConsumerResponse.newBuilder().setStatus(Codes.INVALID_OPERATION.id).build()
    val TOPIC_UNKNOWN: RpcConsumerResponse = RpcConsumerResponse.newBuilder().setStatus(Codes.TOPIC_UNKNOWN.id).build()
  }
}
