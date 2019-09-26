package org.elkd.core

import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.config.ConfigProvider
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.consensus.RaftFactory
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.ds.InMemoryLog
import org.elkd.core.log.LogFactory
import org.elkd.core.log.LogInvoker
import org.elkd.core.runtime.RuntimeModule
import org.elkd.core.runtime.client.command.CommandExecutor
import org.elkd.core.runtime.client.command.CommandRouter
import org.elkd.core.runtime.topic.TopicFactory
import org.elkd.core.runtime.topic.TopicGateway
import org.elkd.core.runtime.topic.TopicRegistry
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
  val logger = Logger.getLogger(Boot::class.java)
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

  /*
   * Configure client module.
   */
  val topicRegistry = TopicRegistry()
  val topicGateway = TopicGateway()
  val topicFactory = TopicFactory(LogFactory())
  val runtimeModule = RuntimeModule(topicRegistry, topicGateway, topicFactory)

  val sysLog = topicFactory.create("@system")
  topicRegistry.add(sysLog)

  /*
   * Configure consensus module `Raft`.
   */
  val consensusModule = ConsensusFacade(RaftFactory.create(config, sysLog.log, clusterMessenger))
  val boot = Boot(config, consensusModule, Server(consensusModule.delegator, CommandRouter(CommandExecutor(consensusModule))))

  logger.info(topicRegistry.toList())

  try {
    with (boot) {
      Runtime.getRuntime().addShutdownHook(Thread(Runnable { shutdown() }))
      start()
      awaitTermination()
    }
  } catch (e: Exception) {
    logger.error("0_o, shutting down: ${e.message}")
  }
}

private fun getConfig(args: Array<String>): Config? {
  return try {
    ConfigProvider.compileConfig(args)
  } catch (e: Exception) {
    return null
  }
}
