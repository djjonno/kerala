package org.elkd.core.server.client

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.client.command.ClientCommandRequestHandler
import org.elkd.core.consensus.RaftDelegate
import org.elkd.core.concurrency.Pools

class ClientService(private val raftDelegate: RaftDelegate,
                    private val clientCommandRequestHandler: ClientCommandRequestHandler) : ElkdClientServiceGrpc.ElkdClientServiceImplBase() {
  init {
    LOG.info("Initializing client service interface")
  }

  override fun clientCommand(request: RpcClientCommandRequest?, responseObserver: StreamObserver<RpcClientCommandResponse>?) {
    Pools.clientCommandThreadPool.execute {
      LOG.info("handling in new client command thread pool - request. ${raftDelegate.supportedOperations}")
      clientCommandRequestHandler.handle(request!!, responseObserver!!)
    }
  }

  private companion object {
    private val LOG = Logger.getLogger(ClientService::class.java)
  }
}
