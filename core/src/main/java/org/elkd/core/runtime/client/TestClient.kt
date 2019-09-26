package org.elkd.core.runtime.client

import io.grpc.ManagedChannelBuilder
import org.elkd.core.server.client.ElkdClientServiceGrpc
import org.elkd.core.server.client.RpcArgPair
import org.elkd.core.server.client.RpcClientCommandRequest
import java.util.concurrent.Future

/**
 * Client which executes calls against server
 */

interface Converter<Source, Target> {
  fun convert(source: Source): Target
}

class A : Converter<String, Int> {
  override fun convert(source: String): Int {
    return 0
  }
}

class B : Converter<Int, String> {
  override fun convert(source: Int): String {
    return ""
  }
}

inline fun <reified T> converter(converters: List<*>): T {
//  return converters.filterIsInstance<T>().first()
  return converters.filterIsInstance<T>().first()
}

typealias G = Converter<Int, String>

fun main() {

  val list = listOf(A(), B())
  list.forEach {
    println(it.javaClass.superclass)
  }

  println(converter<B>(list))


//  val stub = ElkdClientServiceGrpc.newFutureStub(
//      ManagedChannelBuilder.forAddress("localhost", 9007).usePlaintext().build()
//  )
//
//  var count = 0
//  var future: Future<*>
//  do {
//    future = stub.clientCommand(RpcClientCommandRequest.newBuilder()
//        .setCommand("create-topic")
//        .addAllArgs(listOf(RpcArgPair.newBuilder()
//            .setArg("namespace")
//            .setParam("stocks")
//            .build()))
//        .build())
//  } while (++count < 0)
//  println(future.get())
}
