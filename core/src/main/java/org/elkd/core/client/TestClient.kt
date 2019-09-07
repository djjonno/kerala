package org.elkd.core.client

import io.grpc.ManagedChannelBuilder
import org.elkd.core.server.client.ElkdClientServiceGrpc
import org.elkd.core.server.client.RpcClientCommandRequest

/**
 * Client which executes calls against server
 */

fun main() {
  val stub = ElkdClientServiceGrpc.newFutureStub(
      ManagedChannelBuilder.forAddress("localhost", 9191).usePlaintext().build()
  )

  val future = stub.clientCommand(RpcClientCommandRequest.newBuilder()
      .setCommand("create-topic")
      .addAllArgs(listOf("name=stock"))
      .build())

  println(future.get())
}
