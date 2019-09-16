package org.elkd.core.runtime.client.command

import io.grpc.stub.StreamObserver
import org.elkd.core.server.client.RpcClientCommandRequest
import org.elkd.core.server.client.RpcClientCommandResponse
import org.elkd.core.runtime.SystemCommand
import org.elkd.core.runtime.SystemCommandType

/**
 * Executes client commands received from client connections
 */
class CommandRouter(private val commandExecutor: CommandExecutor) {

  fun handle(request: RpcClientCommandRequest, response: StreamObserver<RpcClientCommandResponse>) {
    val command = SystemCommand.builder(SystemCommandType.fromString(request.command)) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
    }

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
  }
}
