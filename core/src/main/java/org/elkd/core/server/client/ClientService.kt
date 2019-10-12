package org.elkd.core.server.client

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.runtime.client.command.ClientCommandHandler
import org.elkd.core.concurrency.Pools

class ClientService(private val clientCommandHandler: ClientCommandHandler): ElkdClientServiceGrpc.ElkdClientServiceImplBase() {
  init {
    logger.info("Initializing client service interface")
  }

  override fun clientCommand(request: RpcClientRequest?, responseObserver: StreamObserver<RpcClientResponse>?) {
    Pools.clientCommandPool.execute {
      clientCommandHandler.handle(request!!, responseObserver!!)
    }
  }

  private companion object {
    private val logger = Logger.getLogger(ClientService::class.java)
  }
}
