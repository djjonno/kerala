package org.elkd.core.consensus

import kotlinx.coroutines.*
import org.apache.log4j.Logger
import org.elkd.core.client.ClientModule
import org.elkd.core.client.TopicRegistry
import org.elkd.core.client.command.ClientCommands
import org.elkd.core.client.model.Bundle
import org.elkd.core.client.model.ClientOpType
import org.elkd.core.client.model.CommandBundle
import org.elkd.core.concurrency.Pools
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.KV
import org.elkd.core.log.LogChangeEvent
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.AppendCommand
import org.elkd.core.system.NotificationCenter
import kotlin.coroutines.CoroutineContext

class ClientRequestController(private val raft: Raft,
                              private val clientModule: ClientModule) : CoroutineScope {
  override val coroutineContext: CoroutineContext
    get() = Job() + Dispatchers.Default

  private val bundleRegistry: MutableMap<Bundle, Long> = mutableMapOf()

  init {
    /* listen to raft state changes */
    initMonitor()
    NotificationCenter.sub(
        NotificationCenter.Channel.RAFT_STATE_CHANGE,
        Pools.clientCommandThreadPool
        ) {
      checkUnsupportedBundles(raft.supportedOperations)
    }
  }

  fun handleCommand(bundle: CommandBundle) {
    if (bundle.opType !in raft.supportedOperations) {
      handleBundleUnsupported(bundle)
      return
    }

    when (bundle.command) {
      ClientCommands.CREATE_TOPIC.id -> handleCreateTopic(bundle)
    }
  }

  private fun handleCreateTopic(bundle: CommandBundle) {
    if (clientModule.topicRegistry.hasTopic(bundle.command)) {
      bundle.onError("${bundle.command} already exists")

    } else {
      val entry = Entry.builder(raft.raftContext.currentTerm, TopicRegistry.SYSTEM_TOPIC_NAME)
          .addKV(KV("cmd", bundle.command))
          .addKV(KV("args", bundle.args.joinToString("&")))
          .build()

      registerBundleForEntry(bundle, entry)
    }
  }

  private fun registerBundleForEntry(bundle: Bundle, entry: Entry) {
    bundleRegistry[bundle] = System.currentTimeMillis() + bundle.timeout
    raft.logComponentProvider.logChangeRegistry.register(entry, LogChangeEvent.COMMIT) {
      bundleRegistry.remove(bundle)
      bundle.onComplete("ok")
    }
    raft.logComponentProvider.logCommandExecutor.execute(AppendCommand.build(entry, LogChangeReason.CLIENT))
  }

  /* Request Monitoring */

  private fun initMonitor() {
    launch {
      do {
        val now = System.currentTimeMillis()
        bundleRegistry
            .filter { entry -> entry.value <= now }
            .map { it.key }
            .forEach(this@ClientRequestController::handleBundleTimeout)
        delay(500)
      } while (true)
    }
  }

  private fun handleBundleTimeout(bundle: Bundle) {
    bundleRegistry.remove(bundle)
    bundle.onError("error: timeout")
  }

  private fun handleBundleUnsupported(bundle: Bundle) {
    bundleRegistry.remove(bundle)
    bundle.onError("error: operation not supported")
  }

  private fun checkUnsupportedBundles(supportedOperations: Set<ClientOpType>) {
    bundleRegistry
        .filter { e -> e.key.opType !in supportedOperations }
        .forEach { (t, _) -> handleBundleUnsupported(t) }
  }

  companion object {
    val log = Logger.getLogger(ClientRequestController::class.java)
  }
}
