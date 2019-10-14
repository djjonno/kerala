package org.elkd.core.server.client

import io.grpc.stub.StreamObserver
import org.elkd.core.runtime.client.command.ClientCommandHandler
import org.elkd.core.runtime.client.stream.ClientStreamHandler

/**
 * ClientService receives connections from clients and routes
 * to the appropriate component.
 */
class ClientService(
    private val clientCommandHandler: ClientCommandHandler,
    private val clientStreamHandler: ClientStreamHandler
) : ElkdClientServiceGrpc.ElkdClientServiceImplBase() {
  override fun clientCommand(request: RpcClientRequest, responseObserver: StreamObserver<RpcClientResponse>) {
    /**
     * Client commands are executed in-serial to ensure appropriate
     * execution order, preventing race conditions.
     */
    clientCommandHandler.handle(request, responseObserver)
  }

  override fun produceTopic(responseObserver: StreamObserver<RpcProducerAck>): StreamObserver<RpcProducerRecord> {
    return clientStreamHandler.establishProducerStream(responseObserver)
  }
}
