package org.ravine.core.runtime.client.ack

import org.ravine.core.server.client.RpcCommandResponse

object ClientACK {
  enum class Codes(val id: Int) {
    OK(0)
  }

  object Rpcs {
    fun ok(message: String): RpcCommandResponse = RpcCommandResponse.newBuilder().setStatus(Codes.OK.id).setResponse(message).build()
  }
}
