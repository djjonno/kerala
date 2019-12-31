package org.kerala.shared.client

import org.kerala.core.server.client.KeralaConsumerResponse

object ConsumerACK {
  enum class Codes(val id: Int) {
    OK(0),
    GENERIC_ERROR(1),
    NETWORK_ERROR(2),
    INVALID_OPERATION(3),
    TOPIC_UNKNOWN(4)
  }

  object Rpcs {
    val OK: KeralaConsumerResponse = KeralaConsumerResponse.newBuilder().setResponseCode(Codes.OK.id).build()
    val GENERIC_ERROR: KeralaConsumerResponse = KeralaConsumerResponse.newBuilder().setResponseCode(Codes.GENERIC_ERROR.id).build()
    val NETWORK_ERROR: KeralaConsumerResponse = KeralaConsumerResponse.newBuilder().setResponseCode(Codes.NETWORK_ERROR.id).build()
    val INVALID_OPERATION: KeralaConsumerResponse = KeralaConsumerResponse.newBuilder().setResponseCode(Codes.INVALID_OPERATION.id).build()
    val TOPIC_UNKNOWN: KeralaConsumerResponse = KeralaConsumerResponse.newBuilder().setResponseCode(Codes.TOPIC_UNKNOWN.id).build()
  }
}
