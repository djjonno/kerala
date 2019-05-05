package org.elkd.core.server.client

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger

class ClientService : ElkdClientServiceGrpc.ElkdClientServiceImplBase() {
  init {
    LOG.info("Initializing client service interface")
  }

  override fun clientCommand(request: RpcClientCommand?, responseObserver: StreamObserver<RpcClientCommandResponse>?) {

  }

  /**
   * This interface is only available to Leader nodes.
   *
   * TODO: We could nest StreamObservers as evaluation stages prior to the handler that actually consumes the RpcKV.
   * TODO: For example, only Leader nodes can receive RpcKV's.
   * TODO: We can make this a configuration problem, configured within this ClientService interface.
   */
  override fun topicPublishingStream(responseObserver: StreamObserver<RpcTopicPublishingNotification>?): StreamObserver<RpcKV> {
    // - authentication
    // - throttling
    // - metric logging
    return super.topicPublishingStream(responseObserver)
  }

  private companion object {
    private val LOG = Logger.getLogger(ClientService::class.java)
  }
}
