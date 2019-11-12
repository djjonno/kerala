package org.kerala.core.server.client

import io.grpc.stub.StreamObserver
import org.kerala.core.runtime.client.ctl.CtlCommandHandler
import org.kerala.core.runtime.client.stream.ClientStreamHandler

/**
 * ClientService receives connections from clients and routes
 * to the appropriate component.
 */
class ClientService(
    private val ctlCommandHandler: CtlCommandHandler,
    private val clientStreamHandler: ClientStreamHandler
) : ClientServiceGrpc.ClientServiceImplBase() {
  override fun clientCommand(request: RpcCommandRequest, responseObserver: StreamObserver<RpcCommandResponse>) {
    /**
     * Client commands are executed in-serial to ensure appropriate
     * execution order, preventing race conditions.
     */
    ctlCommandHandler.handle(request, responseObserver)
  }

  override fun topicConsumer(responseObserver: StreamObserver<RpcConsumerResponse>): StreamObserver<RpcConsumerRequest> {
    return clientStreamHandler.establishConsumerStream(responseObserver)
  }

  override fun topicProducer(responseObserver: StreamObserver<RpcProducerResponse>): StreamObserver<RpcProducerRequest> {
    return clientStreamHandler.establishProducerStream(responseObserver)
  }
}
