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

  val future = stub.clientCommand(RpcClientCommandRequest.newBuilder()
      .setCommand("create-topic")
      .addAllArgs(listOf(RpcArgPair.newBuilder()
          .setArg("namespace")
          .setParam("stocks")
          .build()))
      .build())
  println(future.get())

//  var count = 0
//  var future: Future<RpcClientCommandResponse>? = null
//  do {
//    future = stub.clientCommand(RpcClientCommandRequest.newBuilder()
//        .setCommand("create-topic")
//        .addAllArgs(listOf("namespace=stock+${count++}"))
//        .build())
//    Thread.sleep(1)
//  } while (count < 1000)
//
//  future?.get()
}
