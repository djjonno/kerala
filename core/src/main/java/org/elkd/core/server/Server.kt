package org.elkd.core.server

import io.grpc.ServerBuilder
import java.io.IOException
import java.net.InetAddress
import org.apache.log4j.Logger
import org.elkd.core.consensus.RaftDelegate
import org.elkd.core.runtime.client.command.ClientCommandHandler
import org.elkd.core.runtime.client.stream.ClientStreamHandler
import org.elkd.core.server.client.ClientService
import org.elkd.core.server.cluster.ClusterService
import org.elkd.core.server.converters.ConverterRegistry

class Server(
    val raftDelegate: RaftDelegate,
    val clientCommandHandler: ClientCommandHandler,
    val clientStreamHandler: ClientStreamHandler
) {

  private var rpcClusterServer: io.grpc.Server? = null
  private val converterRegistry = ConverterRegistry.instance

  @Throws(IOException::class)
  fun start(port: Int) {
    rpcClusterServer = ServerBuilder.forPort(port)
        .addService(ClusterService(raftDelegate, converterRegistry))
        .addService(ClientService(clientCommandHandler, clientStreamHandler))
        .build()
        .start()

    LOG.info("Started server on ${InetAddress.getLoopbackAddress()}:$port")
  }

  fun shutdown() {
    LOG.info("stopping server")
    rpcClusterServer?.shutdown()
  }

  @Throws(InterruptedException::class)
  fun awaitTermination() {
    rpcClusterServer?.awaitTermination()
  }

  companion object {
    private val LOG = Logger.getLogger(Server::class.java)
  }
}
