package org.kerala.core.runtime.client.command

import io.grpc.stub.StreamObserver
import org.kerala.core.concurrency.Pools
import org.kerala.core.consensus.OpCategory
import org.kerala.core.server.client.RpcCommandRequest
import org.kerala.core.server.client.RpcCommandResponse

/**
 * Executes client writeCommands received from client connections
 */
class ClientCommandHandler(
    private val clientCommandExecutor: ClientCommandExecutor
) {

  fun handle(request: RpcCommandRequest, response: StreamObserver<RpcCommandResponse>) =
      Pools.clientRequestPool.execute {
        when (request.command) {
          in ClientCommandType.writeCommands -> writeCommandHandler(request, response)
          in ClientCommandType.readCommands -> readCommandHandler(request, response)
          else -> returnError(response, "command `${request.command}` unknown")
        }
      }

  /**
   * Write Commands are executed on @syslog
   */
  private fun writeCommandHandler(request: RpcCommandRequest, response: StreamObserver<RpcCommandResponse>) {
    try {
      clientCommandExecutor.execute(ClientCommandPack(
          command = parseCommand(request),
          onComplete = {
            response.onNext(RpcCommandResponse.newBuilder()
                .setResponse(CLIENT_SUCCESS_RESPONSE)
                .setStatus(CLIENT_SUCCESS_RESPONSE_CODE)
                .build())
            response.onCompleted()
          },
          onError = { message ->
            response.onNext(RpcCommandResponse.newBuilder()
                .setResponse(message)
                .setStatus(CLIENT_ERROR_CODE)
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
  private fun readCommandHandler(request: RpcCommandRequest, response: StreamObserver<RpcCommandResponse>) {
    clientCommandExecutor.execute(ClientCommandPack(
        command = parseCommand(request),
        opCategory = OpCategory.READ,
        onComplete = { message ->
          response.onNext(RpcCommandResponse.newBuilder()
              .setResponse(message)
              .setStatus(CLIENT_SUCCESS_RESPONSE_CODE)
              .build())
          response.onCompleted()
        },
        onError = { error ->
          response.onNext(RpcCommandResponse.newBuilder()
              .setResponse(error)
              .setStatus(CLIENT_ERROR_CODE)
              .build())
          response.onCompleted()
        }
    ))
  }

  private fun returnError(response: StreamObserver<RpcCommandResponse>, message: String?) {
    response.onNext(RpcCommandResponse.newBuilder()
        .setResponse(message)
        .setStatus(CLIENT_ERROR_CODE)
        .build())
    response.onCompleted()
  }

  private fun parseCommand(request: RpcCommandRequest): ClientCommand {
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
