package org.elkd.core.runtime.client.command

import org.elkd.core.concurrency.Pools
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.consensus.OpCategory
import org.elkd.core.runtime.NotificationsHub
import org.elkd.core.runtime.topic.TopicModule

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
      cleanupUnsupportedBundles(consensusFacade.supportedOperations)
    }
  }

  fun execute(bundle: ClientCommandPack) {
    if (bundle.opCategory !in consensusFacade.supportedOperations) {
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
    })
  }

  private fun handleUnsupportedBundleOp(bundle: ClientCommandPack) {
    bundleRegistry.remove(bundle)
    bundle.onError("node op ${bundle.opCategory} not supported")
  }

  private fun cleanupUnsupportedBundles(supportedOps: Set<OpCategory>) {
    bundleRegistry
        .filter { e -> e.opCategory !in supportedOps }
        .forEach { b -> handleUnsupportedBundleOp(b) }
  }

  /* Read Commands Handlers */

  private fun handleReadTopics(bundle: ClientCommandPack) {
    val response = topicModule.topicRegistry.topics.map {
      "$it - index=${it.logFacade.log.lastIndex}"
    }.joinToString(System.lineSeparator())

    bundle.onComplete(response)
  }
}
