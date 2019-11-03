package org.ravine.core.server

import io.grpc.ServerBuilder
import java.io.IOException
import java.net.InetAddress
import org.apache.log4j.Logger
import org.ravine.core.consensus.RaftDelegate
import org.ravine.core.runtime.client.command.ClientCommandHandler
import org.ravine.core.runtime.client.stream.ClientStreamHandler
import org.ravine.core.server.client.ClientService
import org.ravine.core.server.cluster.ClusterService
import org.ravine.core.server.converters.ConverterRegistry

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

    LOGGER.info("started server on ${InetAddress.getLoopbackAddress()}:$port")
  }

  fun shutdown() {
    LOGGER.info("stopping server")
    rpcClusterServer?.shutdown()
  }

  @Throws(InterruptedException::class)
  fun awaitTermination() {
    rpcClusterServer?.awaitTermination()
  }

  companion object {
    private val LOGGER = Logger.getLogger(Server::class.java)
  }
}
