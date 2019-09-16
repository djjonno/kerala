package org.elkd.core.runtime.client

import io.grpc.ManagedChannelBuilder
import org.elkd.core.server.client.ElkdClientServiceGrpc
import org.elkd.core.server.client.RpcArgPair
import org.elkd.core.server.client.RpcClientCommandRequest
import java.util.concurrent.Future

/**
 * Client which executes calls against server
 */
fun main() {
  val stub = ElkdClientServiceGrpc.newFutureStub(
      ManagedChannelBuilder.forAddress("localhost", 9292).usePlaintext().build()
  )

  var count = 0
  var future: Future<*>
  do {
    future = stub.clientCommand(RpcClientCommandRequest.newBuilder()
        .setCommand("create-topic")
        .addAllArgs(listOf(RpcArgPair.newBuilder()
            .setArg("namespace")
            .setParam("weather")
            .build()))
        .build())
  } while (++count < 0)
  println(future.get())
}
