package org.kerala.core

import org.apache.log4j.Logger
import org.kerala.core.config.Config
import org.kerala.core.consensus.ConsensusFacade
import org.kerala.core.consensus.RaftFactory
import org.kerala.core.log.LogFactory
import org.kerala.core.runtime.client.broker.ClusterSetInfo
import org.kerala.core.runtime.client.ctl.CtlCommandExecutor
import org.kerala.core.runtime.client.ctl.CtlCommandHandler
import org.kerala.core.runtime.client.stream.ClientStreamHandler
import org.kerala.core.runtime.topic.TopicFactory
import org.kerala.core.runtime.topic.TopicModule
import org.kerala.core.runtime.topic.TopicRegistry
import org.kerala.core.server.Server
import org.kerala.core.server.cluster.ClusterConnectionPool
import org.kerala.core.server.cluster.ClusterMessenger
import org.kerala.core.server.cluster.ClusterUtils
import org.kerala.core.server.cluster.StaticClusterSet

/**
 * Platform Boot
 *
 * Bootstrapping module - configure all runtime dependencies.
 */
internal class Boot(
    private val consensusFacade: ConsensusFacade,
    private val server: Server
) {

  fun start() {
    val port: Int = Environment.config[Config.KEY_PORT]
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
   * A blocking call, awaiting runtime resource de-allocation.  Call this prior to shutdown.
   */
  fun awaitTermination() {
    server.awaitTermination()
  }
}

/**
 * Bootstrapping
 */
fun main(args: Array<String>) {
  val logger = Logger.getLogger(Boot::class.java)
  try {
    /* Config is lazy loaded, access to initialize */
    Environment.args = args
    Environment.config
  } catch (e: Exception) { return }

  /*
   * Cluster Set
   *
   * Currently only support for static cluster membership - no changes during runtime.
   */
  val clusterSet = StaticClusterSet.builder(ClusterUtils.buildSelfNode())
      .withString(Environment.config[Config.KEY_CLUSTER])
      .build()
  val clusterConnectionPool = ClusterConnectionPool(clusterSet).apply { initialize() }
  val clusterInfo = ClusterSetInfo(clusterSet)

  /*
   * ClusterMessenger
   *
   * Communication interface for node-node messaging.
   */
  val clusterMessenger = ClusterMessenger(clusterConnectionPool)

  /*
   * Configure client systems.
   */
  val topicModule = TopicModule(TopicRegistry(), TopicFactory(LogFactory()), clusterInfo)
  val consensusFacade = ConsensusFacade(RaftFactory.create(topicModule, clusterMessenger))
  val clientStreamHandler = ClientStreamHandler(consensusFacade, topicModule)
  val clientCommandHandler = CtlCommandHandler(CtlCommandExecutor(consensusFacade, topicModule, clusterInfo))

  try {
    with(Boot(consensusFacade, Server(consensusFacade.delegator, clientCommandHandler, clientStreamHandler))) {
      Runtime.getRuntime().addShutdownHook(Thread(Runnable { shutdown() }))
      start()
      awaitTermination()
    }
  } catch (e: Exception) {
    logger.error("0_o, shutting down: ${e.message}")
  }
}
