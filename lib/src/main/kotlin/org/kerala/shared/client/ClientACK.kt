package org.kerala.shared.client

import org.kerala.core.server.client.KeralaCommandResponse

object ClientACK {
  enum class Codes(val id: Int) {
    OK(0),
    ERROR(1)
  }

  object Rpcs {
    fun ok(message: String): KeralaCommandResponse = KeralaCommandResponse.newBuilder().setStatus(Codes.OK.id).setResponse(message).build()
    fun error(message: String, status: Int): KeralaCommandResponse = KeralaCommandResponse.newBuilder().setStatus(status).setResponse(message).build()
  }
}
