package org.elkd.core

import org.elkd.core.client.ClientModule
import org.elkd.core.client.TopicRegistry
import org.elkd.core.client.handlers.CommandRequestHandler
import org.elkd.core.config.Config
import org.elkd.core.config.ConfigProvider
import org.elkd.core.consensus.ClientRequestController
import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.RaftFactory
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.InMemoryLog
import org.elkd.core.log.LogComponentProvider
import org.elkd.core.log.LogInvoker
import org.elkd.core.server.Server
import org.elkd.core.server.cluster.ClusterConnectionPool
import org.elkd.core.server.cluster.ClusterMessenger
import org.elkd.core.server.cluster.ClusterUtils
import org.elkd.core.server.cluster.StaticClusterSet

/**
 * Platform Boot
 *
 * Bootstrapping module - configure all system dependencies.
 */
internal class Boot(private val config: Config,
                    private val raft: Raft,
                    private val server: Server) {

  fun start() {
    val port = config.getAsInteger(Config.KEY_PORT)
    server.start(port)
    raft.initialize()
  }

  /**
   * Free-up system resources prior to shutdown.
   */
  fun shutdown() {
    server.shutdown()
    /* send a shutdown notification. e.g The Log may want to know to persist any in-memory cache structures. */
  }

  /**
   * A blocking call, awaiting system resource deallocation.  Call this prior to shutdown.
   */
  fun awaitTermination() {
    server.awaitTermination()
  }
}

/**
 * Elkd Bootstrapping
 */
fun main(args: Array<String>) {
  val config = getConfig(args) ?: return

  /*
   * Cluster Set
   *
   * Currently only support for static cluster membership - no changes during runtime.
   */
  val clusterSet = StaticClusterSet.builder(ClusterUtils.buildSelfNode(config))
      .withString(config.get(Config.KEY_CLUSTER_SET))
      .build()
  val clusterConnectionPool = ClusterConnectionPool(clusterSet)
  clusterConnectionPool.initialize()

  /*
   * ClusterMessenger
   *
   * Communication interface for node-node messaging.
   */
  val clusterMessenger = ClusterMessenger(clusterConnectionPool)

  val logProvider = LogComponentProvider(LogInvoker<Entry>(InMemoryLog()))

  /*
   * Configure consensus module `Raft`.
   */
  val raft = RaftFactory.create(config, logProvider, clusterMessenger)

  /*
   * Configure client module.
   */
  val clientModule = ClientModule(TopicRegistry())

  val boot = Boot(config, raft, Server(raft.delegator, CommandRequestHandler(ClientRequestController(raft))))

  try {
    Runtime.getRuntime().addShutdownHook(Thread(Runnable { boot.shutdown() }))
    boot.start()
    boot.awaitTermination()
  } catch (e: Exception) {
    e.printStackTrace()
  }
}

private fun getConfig(args: Array<String>): Config? {
  return try {
    ConfigProvider.compileConfig(args)
  } catch (e: Exception) {
    return null
  }
}
