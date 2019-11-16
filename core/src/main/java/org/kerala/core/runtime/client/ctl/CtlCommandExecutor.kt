package org.kerala.core.runtime.client.ctl

import org.kerala.core.Environment
import org.kerala.core.concurrency.Pools
import org.kerala.core.config.Config
import org.kerala.core.consensus.ConsensusFacade
import org.kerala.core.consensus.OpCategory
import org.kerala.core.runtime.NotificationsHub
import org.kerala.core.runtime.client.broker.ClusterSetInfo
import org.kerala.core.runtime.topic.TopicModule
import org.kerala.shared.client.CtlClusterDescription
import org.kerala.shared.client.CtlSuccessResponse
import org.kerala.shared.client.CtlNode
import org.kerala.shared.client.CtlReadTopics
import org.kerala.shared.client.CtlTopicMeta
import org.kerala.shared.json.GsonUtils
import java.time.Duration

class CtlCommandExecutor(
    private val consensusFacade: ConsensusFacade,
    private val topicModule: TopicModule,
    private val clusterSetInfo: ClusterSetInfo
) {
  private val packRegistry: MutableSet<CtlCommandPack> = mutableSetOf()
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

  fun execute(pack: CtlCommandPack) {
    if (!consensusFacade.supportsCategory(pack.opCategory)) {
      handleUnsupportedBundleOp(pack)
      return
    }

    when (pack.opCategory) {
      OpCategory.READ -> executeReadCommand(pack)
      OpCategory.WRITE -> writeCommandToSyslog(pack)
    }
  }

  private fun executeReadCommand(pack: CtlCommandPack) {
    when (pack.command.command) {
      CtlCommandType.DESCRIBE_TOPICS.id -> handleReadTopics(pack)
      CtlCommandType.DESCRIBE_CLUSTER.id -> handleClusterInfo(pack)
      else -> throw CtlCommandUnknownException(pack.command.command)
    }
  }

  private fun writeCommandToSyslog(pack: CtlCommandPack) {
    packRegistry.add(pack)
    consensusFacade.writeToTopic(topicModule.syslog, pack.command.kvs, {
      packRegistry.remove(pack)
      pack.onComplete(gson.toJson(CtlSuccessResponse("committed ✓")))
    }, {
      packRegistry.remove(pack)
      /* TODO: Need to handle exceptions appropriately here has log change may have just timed-out. */
      handleUnsupportedBundleOp(pack)
    }, CLIENT_COMMAND_TIMEOUT)
  }

  private fun handleUnsupportedBundleOp(pack: CtlCommandPack) {
    packRegistry.remove(pack)
    throw CtlCommandOperationException(pack.opCategory)
  }

  private fun cleanupUnsupportedBundles() {
    packRegistry
        .filter { e -> consensusFacade.supportsCategory(e.opCategory) }
        .forEach { b -> handleUnsupportedBundleOp(b) }
  }

  private fun handleReadTopics(pack: CtlCommandPack) {
    pack.onComplete(gson.toJson(CtlReadTopics(topicModule.topicRegistry.topics.map {
      CtlTopicMeta(it.id, it.namespace, it.logFacade.log.commitIndex)
    })))
  }

  private fun handleClusterInfo(pack: CtlCommandPack) {
    pack.onComplete(gson.toJson(CtlClusterDescription(clusterSetInfo.clusterSet.allNodes.map {
      CtlNode(it.id, it.host, it.port, it == clusterSetInfo.leader)
    })))
  }

  companion object {
    val CLIENT_COMMAND_TIMEOUT: Duration = Duration.ofSeconds(Environment.config[Config.KEY_CLIENT_COMMAND_TIMEOUT])
  }
}
