package org.kerala.ctl

import io.grpc.ManagedChannel
import org.kerala.core.server.client.ClientServiceGrpc
import org.kerala.core.server.client.RpcArgPair
import org.kerala.core.server.client.RpcCommandRequest
import org.kerala.core.server.client.RpcCommandResponse

fun sendCommand(
    channel: ManagedChannel,
    command: String,
    argPairs: List<RpcArgPair>
): RpcCommandResponse {
  val stub = ClientServiceGrpc.newFutureStub(channel)
  return stub.clientCommand(RpcCommandRequest.newBuilder()
      .setCommand(command)
      .addAllArgs(argPairs)
      .build()).get()
}
