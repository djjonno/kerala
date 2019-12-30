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
) : KeralaClientServiceGrpc.KeralaClientServiceImplBase() {
  override fun keralaClientCommand(request: KeralaCommandRequest, responseObserver: StreamObserver<KeralaCommandResponse>) {
    /**
     * Client commands are executed in-serial to ensure appropriate
     * execution order, preventing race conditions.
     */
    ctlCommandHandler.handle(request, responseObserver)
  }

  override fun keralaTopicConsumer(responseObserver: StreamObserver<KeralaConsumerResponse>): StreamObserver<KeralaConsumerRequest> {
    return clientStreamHandler.establishConsumerStream(responseObserver)
  }

  override fun keralaTopicProducer(responseObserver: StreamObserver<KeralaProducerResponse>): StreamObserver<KeralaProducerRequest> {
    return clientStreamHandler.establishProducerStream(responseObserver)
  }
}
