package org.elkd.core.client.handlers

import io.grpc.stub.StreamObserver
import org.elkd.core.client.model.CommandBundle
import org.elkd.core.server.client.RpcClientCommandRequest
import org.elkd.core.server.client.RpcClientCommandResponse
import org.elkd.core.system.SystemCommand
import org.elkd.core.system.SystemCommands

/**
 * Executes client commands received from client connections
 */
class CommandRouter(private val commandReceiver: CommandReceiver) {

  fun handle(request: RpcClientCommandRequest, response: StreamObserver<RpcClientCommandResponse>) {
    val command = SystemCommand.builder(SystemCommands.fromString(request.command)) {
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

    commandReceiver.receive(cmdBundle)
  }
}
