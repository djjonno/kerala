package org.kerala.ctl

import io.grpc.ManagedChannel
import org.kerala.core.server.client.KeralaClientServiceGrpc
import org.kerala.core.server.client.KeralaArgPair
import org.kerala.core.server.client.KeralaCommandRequest
import org.kerala.core.server.client.KeralaCommandResponse

fun sendCommand(
    channel: ManagedChannel,
    command: String,
    argPairs: List<KeralaArgPair> = emptyList()
): KeralaCommandResponse {
  val stub = KeralaClientServiceGrpc.newFutureStub(channel)
  return stub.keralaClientCommand(KeralaCommandRequest.newBuilder()
      .setCommand(command)
      .addAllArgs(argPairs)
      .build()).get()
}
