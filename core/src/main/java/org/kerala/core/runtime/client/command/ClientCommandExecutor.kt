package org.kerala.core.runtime.client.command

import org.kerala.core.Environment
import org.kerala.core.concurrency.Pools
import org.kerala.core.config.Config
import org.kerala.core.consensus.ConsensusFacade
import org.kerala.core.consensus.OpCategory
import org.kerala.core.runtime.NotificationsHub
import org.kerala.core.runtime.topic.TopicModule
import java.time.Duration

class ClientCommandExecutor(
    private val consensusFacade: ConsensusFacade,
    private val topicModule: TopicModule
) {
  private val bundleRegistry: MutableSet<ClientCommandPack> = mutableSetOf()

  init {
    /* listen to consensus state changes */
    NotificationsHub.sub(
        NotificationsHub.Channel.CONSENSUS_CHANGE,
        Pools.clientRequestPool
    ) {
      cleanupUnsupportedBundles()
    }
  }

  fun execute(bundle: ClientCommandPack) {
    if (!consensusFacade.supportsCategory(bundle.opCategory)) {
      handleUnsupportedBundleOp(bundle)
      return
    }

    when (bundle.opCategory) {
      OpCategory.READ -> executeReadCommand(bundle)
      OpCategory.WRITE -> writeCommandToSyslog(bundle)
    }
  }

  private fun executeReadCommand(bundle: ClientCommandPack) {
    when (bundle.command.command) {
      ClientCommandType.READ_TOPICS.id -> handleReadTopics(bundle)
      else -> bundle.onError("command `${bundle.command}` unknown")
    }
  }

  private fun writeCommandToSyslog(bundle: ClientCommandPack) {
    bundleRegistry.add(bundle)
    consensusFacade.writeToTopic(topicModule.syslog, bundle.command.kvs, {
      bundleRegistry.remove(bundle)
      bundle.onComplete("")
    }, {
      bundleRegistry.remove(bundle)
      handleUnsupportedBundleOp(bundle)
    }, CLIENT_COMMAND_TIMEOUT)
  }

  private fun handleUnsupportedBundleOp(bundle: ClientCommandPack) {
    bundleRegistry.remove(bundle)
    bundle.onError("node op ${bundle.opCategory} not supported")
  }

  private fun cleanupUnsupportedBundles() {
    bundleRegistry
        .filter { e -> consensusFacade.supportsCategory(e.opCategory) }
        .forEach { b -> handleUnsupportedBundleOp(b) }
  }

  private fun handleReadTopics(bundle: ClientCommandPack) {
    val response = topicModule.topicRegistry.topics.map {
      "$it - index=${it.logFacade.log.lastIndex}"
    }.joinToString(System.lineSeparator())

    bundle.onComplete(response)
  }

  companion object {
    val CLIENT_COMMAND_TIMEOUT: Duration = Duration.ofSeconds(Environment.config[Config.KEY_CLIENT_COMMAND_TIMEOUT])
  }
}
