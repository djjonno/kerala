package org.elkd.core.runtime.client.command

import io.grpc.stub.StreamObserver
import org.elkd.core.server.client.RpcClientCommandRequest
import org.elkd.core.server.client.RpcClientCommandResponse
import java.util.UUID

/**
 * Executes client commands received from client connections
 */
class CommandRouter(private val commandExecutor: CommandExecutor) {

  fun handle(request: RpcClientCommandRequest, response: StreamObserver<RpcClientCommandResponse>) {
    try {
      val command = parseCommand(request)
      val cmdBundle = CommandBundle(
          command = command,
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
      response.onError(e)
    }
  }

  @Throws(Exception::class)
  private fun parseCommand(request: RpcClientCommandRequest): Command {
    if (request.command !in CommandType.values().map { it.id }) {
      throw Exception("Command `${request.command}` unknown")
    }

    return Command.builder(request.command) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
    }
  }
}
