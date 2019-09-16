package org.elkd.core.server.client

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.runtime.client.command.CommandRouter
import org.elkd.core.concurrency.Pools

class ClientService(private val commandRouter: CommandRouter): ElkdClientServiceGrpc.ElkdClientServiceImplBase() {
  init {
    logger.info("Initializing client service interface")
  }

  override fun clientCommand(request: RpcClientCommandRequest?, responseObserver: StreamObserver<RpcClientCommandResponse>?) {
    Pools.clientCommandThreadPool.execute {
      commandRouter.handle(request!!, responseObserver!!)
    }
  }

  private companion object {
    private val logger = Logger.getLogger(ClientService::class.java)
  }
}
