package org.kerala.core.runtime.client.command

import org.kerala.core.Environment
import org.kerala.core.concurrency.Pools
import org.kerala.core.config.Config
import org.kerala.core.consensus.ConsensusFacade
import org.kerala.core.consensus.OpCategory
import org.kerala.core.runtime.NotificationsHub
import org.kerala.core.runtime.client.broker.ClusterSetInfo
import org.kerala.core.runtime.topic.TopicModule
import org.kerala.shared.client.ClientSuccessResponse
import org.kerala.shared.client.ClusterInfo
import org.kerala.shared.client.Node
import org.kerala.shared.client.ReadTopics
import org.kerala.shared.client.TopicMeta
import org.kerala.shared.json.GsonUtils
import java.time.Duration

class ClientCommandExecutor(
    private val consensusFacade: ConsensusFacade,
    private val topicModule: TopicModule,
    private val clusterSetInfo: ClusterSetInfo
) {
  private val packRegistry: MutableSet<ClientCommandPack> = mutableSetOf()
  private val gson = GsonUtils.buildGson()

  init {
    /* listen to consensus state changes */
    NotificationsHub.sub(
        NotificationsHub.Channel.CONSENSUS_CHANGE,
        Pools.clientRequestPool
    ) {
      cleanupUnsupportedBundles()
    }
  }

  fun execute(pack: ClientCommandPack) {
    if (!consensusFacade.supportsCategory(pack.opCategory)) {
      handleUnsupportedBundleOp(pack)
      return
    }

    when (pack.opCategory) {
      OpCategory.READ -> executeReadCommand(pack)
      OpCategory.WRITE -> writeCommandToSyslog(pack)
    }
  }

  private fun executeReadCommand(pack: ClientCommandPack) {
    when (pack.command.command) {
      ClientCommandType.READ_TOPICS.id -> handleReadTopics(pack)
      ClientCommandType.CLUSTER_INFO.id -> handleClusterInfo(pack)
      else -> pack.onError("command `${pack.command}` unknown")
    }
  }

  private fun writeCommandToSyslog(pack: ClientCommandPack) {
    packRegistry.add(pack)
    consensusFacade.writeToTopic(topicModule.syslog, pack.command.kvs, {
      packRegistry.remove(pack)
      pack.onComplete(gson.toJson(ClientSuccessResponse("command committed")))
    }, {
      packRegistry.remove(pack)
      handleUnsupportedBundleOp(pack)
    }, CLIENT_COMMAND_TIMEOUT)
  }

  private fun handleUnsupportedBundleOp(pack: ClientCommandPack) {
    packRegistry.remove(pack)
    pack.onError("node op ${pack.opCategory} not supported")
  }

  private fun cleanupUnsupportedBundles() {
    packRegistry
        .filter { e -> consensusFacade.supportsCategory(e.opCategory) }
        .forEach { b -> handleUnsupportedBundleOp(b) }
  }

  private fun handleReadTopics(pack: ClientCommandPack) {
    pack.onComplete(gson.toJson(ReadTopics(topicModule.topicRegistry.topics.map {
      TopicMeta(it.namespace, it.logFacade.log.commitIndex)
    })))
  }

  private fun handleClusterInfo(pack: ClientCommandPack) {
    pack.onComplete(gson.toJson(ClusterInfo(clusterSetInfo.clusterSet.allNodes.map {
      Node(it.id, it.host, it.port, it == clusterSetInfo.leader)
    })))
  }

  companion object {
    val CLIENT_COMMAND_TIMEOUT: Duration = Duration.ofSeconds(Environment.config[Config.KEY_CLIENT_COMMAND_TIMEOUT])
  }
}
