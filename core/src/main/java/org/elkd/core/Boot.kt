package org.elkd.core

import org.elkd.core.runtime.client.ClientModule
import org.elkd.core.runtime.client.command.CommandRouter
import org.elkd.core.config.Config
import org.elkd.core.config.ConfigProvider
import org.elkd.core.runtime.client.command.CommandExecutor
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.consensus.RaftFactory
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.InMemoryLog
import org.elkd.core.log.LogFacade
import org.elkd.core.log.LogInvoker
import org.elkd.core.server.Server
import org.elkd.core.server.cluster.ClusterConnectionPool
import org.elkd.core.server.cluster.ClusterMessenger
import org.elkd.core.server.cluster.ClusterUtils
import org.elkd.core.server.cluster.StaticClusterSet

/**
 * Platform Boot
 *
 * Bootstrapping module - configure all runtime dependencies.
 */
internal class Boot(private val config: Config,
                    private val consensusFacade: ConsensusFacade,
                    private val server: Server) {

  fun start() {
    val port = config.getAsInteger(Config.KEY_PORT)
    server.start(port)
    consensusFacade.initialize()
  }

  /**
   * Free-up runtime resources prior to shutdown.
   */
  fun shutdown() {
    server.shutdown()
    /* send a shutdown notification. e.g The Log may want to know to persist any in-memory cache structures. */
  }

  /**
   * A blocking call, awaiting runtime resource deallocation.  Call this prior to shutdown.
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

  val logModule = LogFacade(LogInvoker<Entry>(InMemoryLog()))

  /*
   * Configure consensus module `Raft`.
   */
  val consensusModule = ConsensusFacade(RaftFactory.create(config, logModule, clusterMessenger))

  /*
   * Configure client module.
   */
  val clientModule = ClientModule()

  val boot = Boot(config, consensusModule, Server(consensusModule.delegator, CommandRouter(CommandExecutor(consensusModule))))

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
