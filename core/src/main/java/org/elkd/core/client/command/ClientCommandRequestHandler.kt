package org.elkd.core.client.command

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.client.model.ClientOpType
import org.elkd.core.client.model.CommandBundle
import org.elkd.core.consensus.ClientRequestController
import org.elkd.core.server.client.RpcClientCommandRequest
import org.elkd.core.server.client.RpcClientCommandResponse

/**
 * Executes client commands received from client connections
 */
class ClientCommandRequestHandler(val clientRequestController: ClientRequestController) {
  fun handle(request: RpcClientCommandRequest, response: StreamObserver<RpcClientCommandResponse>) {
    /* 1. validate command */

    /* 2. build CommandBundle */
    val cmdBundle = CommandBundle(
        command = request.command,
        args = request.argsList.toList(),
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

    clientRequestController.handleCommand(cmdBundle)
  }

  companion object {
    private var log = Logger.getLogger(ClientCommandRequestHandler::class.java)
  }
}
