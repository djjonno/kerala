package org.elkd.core.runtime.client.command

import io.grpc.stub.StreamObserver
import org.elkd.core.consensus.OpCategory
import org.elkd.core.server.client.RpcClientRequest
import org.elkd.core.server.client.RpcClientResponse

/**
 * Executes client writeCommands received from client connections
 */
class ClientCommandHandler(
    private val clientCommandExecutor: ClientCommandExecutor
) {

  fun handle(request: RpcClientRequest, response: StreamObserver<RpcClientResponse>) = when (request.command) {
    in ClientCommandType.writeCommands -> writeCommandHandler(request, response)
    in ClientCommandType.readCommands -> readCommandHandler(request, response)
    else -> returnError(response, "command `${request.command}` unknown")
  }

  /**
   * Write Commands are executed on @syslog
   */
  private fun writeCommandHandler(request: RpcClientRequest, response: StreamObserver<RpcClientResponse>) {
    try {
      clientCommandExecutor.execute(ClientCommandPack(
          command = parseCommand(request),
          onComplete = {
            response.onNext(RpcClientResponse.newBuilder()
                .setResponse(CLIENT_SUCCESS_RESPONSE)
                .setCode(CLIENT_SUCCESS_RESPONSE_CODE)
                .build())
            response.onCompleted()
          },
          onError = { message ->
            response.onNext(RpcClientResponse.newBuilder()
                .setResponse(message)
                .setCode(CLIENT_ERROR_CODE)
                .build())
            response.onCompleted()
          }
      ))
    } catch (e: Exception) {
      returnError(response, e.message)
    }
  }

  /**
   * Read commands must only read state, write operations are not allowed.
   */
  private fun readCommandHandler(request: RpcClientRequest, response: StreamObserver<RpcClientResponse>) {
    clientCommandExecutor.execute(ClientCommandPack(
        command = parseCommand(request),
        opCategory = OpCategory.READ,
        onComplete = { message ->
          response.onNext(RpcClientResponse.newBuilder()
              .setResponse(message)
              .setCode(CLIENT_SUCCESS_RESPONSE_CODE)
              .build())
          response.onCompleted()
        },
        onError = { error ->
          response.onNext(RpcClientResponse.newBuilder()
              .setResponse(error)
              .setCode(CLIENT_ERROR_CODE)
              .build())
          response.onCompleted()
        }
    ))
  }

  private fun returnError(response: StreamObserver<RpcClientResponse>, message: String?) {
    response.onNext(RpcClientResponse.newBuilder()
        .setResponse(message)
        .setCode(CLIENT_ERROR_CODE)
        .build())
    response.onCompleted()
  }

  private fun parseCommand(request: RpcClientRequest): ClientCommand {
    val type = ClientCommandType.fromId(request.command)
    return type.parser(type, request)
  }

  private companion object {
    const val CLIENT_SUCCESS_RESPONSE = "ok"
    const val CLIENT_SUCCESS_RESPONSE_CODE = 0

    /* Invalid command */
    const val CLIENT_ERROR_CODE = 1
  }
}
