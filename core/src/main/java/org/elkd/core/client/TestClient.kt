package org.elkd.core.client

import io.grpc.ManagedChannelBuilder
import org.elkd.core.server.client.ElkdClientServiceGrpc
import org.elkd.core.server.client.RpcArgPair
import org.elkd.core.server.client.RpcClientCommandRequest
import org.elkd.core.server.client.RpcClientCommandResponse
import java.util.concurrent.Future

/**
 * Client which executes calls against server
 */
fun main() {
  val stub = ElkdClientServiceGrpc.newFutureStub(
      ManagedChannelBuilder.forAddress("localhost", 9191).usePlaintext().build()
  )

  var count = 0
  do {
    val future = stub.clientCommand(RpcClientCommandRequest.newBuilder()
        .setCommand("create-topic")
        .addAllArgs(listOf(RpcArgPair.newBuilder()
            .setArg("namespace")
            .setParam("dummy")
            .build()))
        .build())
    println(future.get())
  } while (++count < 0)
}
