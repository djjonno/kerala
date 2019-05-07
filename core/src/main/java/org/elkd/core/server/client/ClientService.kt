package org.elkd.core.server.client

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger

class ClientService : ElkdClientServiceGrpc.ElkdClientServiceImplBase() {
  init {
    LOG.info("Initializing client service interface")
  }

  private companion object {
    private val LOG = Logger.getLogger(ClientService::class.java)
  }
}
