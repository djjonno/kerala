package org.kerala.shared.client

import org.kerala.core.server.client.KeralaProducerResponse

object ProducerACK {
  enum class Codes(val id: Int) {
    OK(0),
    GENERIC_ERROR(1),
    NETWORK_ERROR(2),
    INVALID_OPERATION(3),
    TOPIC_UNKNOWN(4),
    OPERATION_TIMEOUT(5)
  }

  object Rpcs {
    val OK: KeralaProducerResponse = KeralaProducerResponse.newBuilder().setStatus(Codes.OK.id).build()
    val GENERIC_ERROR: KeralaProducerResponse = KeralaProducerResponse.newBuilder().setStatus(Codes.GENERIC_ERROR.id).build()
    val NETWORK_ERROR: KeralaProducerResponse = KeralaProducerResponse.newBuilder().setStatus(Codes.NETWORK_ERROR.id).build()
    val OPERATION_INVALID: KeralaProducerResponse = KeralaProducerResponse.newBuilder().setStatus(Codes.INVALID_OPERATION.id).build()
    val TOPIC_UNKNOWN: KeralaProducerResponse = KeralaProducerResponse.newBuilder().setStatus(Codes.TOPIC_UNKNOWN.id).build()
    val OPERATION_TIMEOUT: KeralaProducerResponse = KeralaProducerResponse.newBuilder().setStatus(Codes.OPERATION_TIMEOUT.id).build()
  }
}
