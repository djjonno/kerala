package org.kerala.core.runtime.client.ack

import org.kerala.core.server.client.RpcCommandResponse

object ClientACK {
  enum class Codes(val id: Int) {
    OK(0),
    ERROR(1)
  }

  object Rpcs {
    fun ok(message: String): RpcCommandResponse = RpcCommandResponse.newBuilder().setStatus(Codes.OK.id).setResponse(message).build()
    fun error(message: String): RpcCommandResponse = RpcCommandResponse.newBuilder().setStatus(Codes.ERROR.id).setResponse(message).build()
  }
}
