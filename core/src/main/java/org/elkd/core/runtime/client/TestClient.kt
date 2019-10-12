package org.elkd.core.runtime.client

import io.grpc.ManagedChannelBuilder
import org.elkd.core.server.client.ElkdClientServiceGrpc
import org.elkd.core.server.client.RpcArgPair
import org.elkd.core.server.client.RpcClientRequest
import org.elkd.core.server.client.RpcClientResponse
import org.elkd.core.server.cluster.ElkdClusterServiceGrpc
import org.elkd.core.server.cluster.RpcLogTail
import org.elkd.core.server.cluster.RpcRequestVoteRequest
import java.util.concurrent.Executors
import java.util.concurrent.Future

fun main() {
  createTopic(9191)

//  requestVotesMultiple(9191)
}

private fun requestVotesMultiple(port: Int) {
  val stub = ElkdClusterServiceGrpc.newFutureStub(
      ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()
  )

  val newFixedThreadPool = Executors.newFixedThreadPool(2)
  listOf("localhost:9292", "localhost:9393").map {
    RpcRequestVoteRequest.newBuilder()
        .setTerm(10)
        .setCandidateId(it)
        .addLogTails(RpcLogTail.newBuilder()
            .setTopicId("@syslog")
            .setLastLogIndex(1)
            .setLastLogTerm(1)
            .build())
        .build()
  }.map {
    stub.requestVote(it)
  }.forEach {
    val response = it.get()
    println("${response.term} ${response.voteGranted}")
  }

  Thread.sleep(5000)
}

private fun createTopic(port: Int) {
  val stub = ElkdClientServiceGrpc.newFutureStub(
      ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()
  )

  var count = 0
  var future: Future<RpcClientResponse>
  do {
    future = stub.clientCommand(RpcClientRequest.newBuilder()
        .setCommand("create-topic")
        .addAllArgs(listOf(RpcArgPair.newBuilder()
            .setArg("namespace")
            .setParam("sensor_ambient")
            .build()))
        .build())
  } while (++count < 0)
  println(future.get().response)
}
