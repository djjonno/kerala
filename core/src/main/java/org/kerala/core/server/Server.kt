package org.kerala.core.server

import io.grpc.ServerBuilder
import org.kerala.core.consensus.RaftDelegate
import org.kerala.core.runtime.client.ctl.CtlCommandHandler
import org.kerala.core.runtime.client.stream.ClientStreamHandler
import org.kerala.core.server.client.ClientService
import org.kerala.core.server.cluster.ClusterService
import org.kerala.core.server.converters.ConverterRegistry
import org.kerala.shared.logger
import java.io.IOException
import java.net.InetAddress

class Server(
    private val raftDelegate: RaftDelegate,
    private val ctlCommandHandler: CtlCommandHandler,
    private val clientStreamHandler: ClientStreamHandler
) {

  private var rpcClusterServer: io.grpc.Server? = null
  private val converterRegistry = ConverterRegistry.instance

  @Throws(IOException::class)
  fun start(port: Int) {
    rpcClusterServer = ServerBuilder.forPort(port)
        .addService(ClusterService(raftDelegate, converterRegistry))
        .addService(ClientService(ctlCommandHandler, clientStreamHandler))
        .build()
        .start()

    logger("started server on ${InetAddress.getLoopbackAddress()}:$port")
  }

  fun shutdown() {
    logger("stopping server")
    rpcClusterServer?.shutdown()
  }

  @Throws(InterruptedException::class)
  fun awaitTermination() {
    rpcClusterServer?.awaitTermination()
  }
}
