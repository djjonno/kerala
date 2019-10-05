package org.elkd.core.runtime.client.command

import io.grpc.stub.StreamObserver
import org.elkd.core.server.client.RpcClientCommandRequest
import org.elkd.core.server.client.RpcClientCommandResponse

/**
 * Executes client commands received from client connections
 */
class CommandRouter(
    private val commandExecutor: CommandExecutor,
    private val transformers: Map<CommandType, CommandTransformer>
) {

  fun handle(request: RpcClientCommandRequest, response: StreamObserver<RpcClientCommandResponse>) {
    try {
      val cmdBundle = CommandBundle(
          command = buildCommand(request),
          onComplete = { message ->
            response.onNext(RpcClientCommandResponse.newBuilder()
                .setResponse(message).build())
            response.onCompleted()
          },
          onError = { message ->
            response.onNext(RpcClientCommandResponse.newBuilder()
                .setResponse(message).build())
            response.onCompleted()
          }
      )
      commandExecutor.receive(cmdBundle)
    } catch (e: Exception) {
      response.onNext(RpcClientCommandResponse.newBuilder()
          .setResponse(e.message).build())
      response.onCompleted()
    }
  }

  @Throws(Exception::class)
  private fun buildCommand(request: RpcClientCommandRequest): Command {
    val type = try {
      CommandType.fromString(request.command)
    } catch (e: Exception) {
      throw Exception("Command `${request.command}` unknown")
    }

    return transformers.getOrDefault(type, NoOpCommandTransformer())(type, request)
  }
}
