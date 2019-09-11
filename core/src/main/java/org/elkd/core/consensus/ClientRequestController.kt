package org.elkd.core.consensus

import kotlinx.coroutines.*
import org.apache.log4j.Logger
import org.elkd.core.client.model.Package
import org.elkd.core.client.model.OperationCategory
import org.elkd.core.client.model.CommandPackage
import org.elkd.core.concurrency.Pools
import org.elkd.core.log.LogChangeEvent
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.AppendCommand
import org.elkd.core.system.NotificationCenter
import kotlin.coroutines.CoroutineContext

class ClientRequestController(private val raft: Raft) : CoroutineScope {
  override val coroutineContext: CoroutineContext
    get() = Job() + Dispatchers.Default

  private val packageRegistry: MutableMap<Package, Long> = mutableMapOf()

  init {
    /* listen to raft state changes */
    initMonitor()
    initListener()
  }

  private fun initListener() {
    NotificationCenter.sub(
        NotificationCenter.Channel.RAFT_STATE_CHANGE,
        Pools.clientCommandThreadPool
    ) {
      checkUnsupportedBundles(raft.supportedOperations)
    }
  }

  fun receive(bundle: CommandPackage) {
    if (bundle.operationCategory !in raft.supportedOperations) {
      handleBundleUnsupported(bundle)
      return
    }

    executeCommandBundle(bundle)
  }

  private fun executeCommandBundle(bundle: CommandPackage) {
    val entry = bundle.command.asEntry(raft.raftContext.currentTerm)
    packageRegistry[bundle] = System.currentTimeMillis() + bundle.timeout
    raft.logComponentProvider.logChangeRegistry.register(entry, LogChangeEvent.COMMIT) {
      packageRegistry.remove(bundle)
      bundle.onComplete("ok")
    }
    raft.logComponentProvider.logCommandExecutor.execute(AppendCommand.build(entry, LogChangeReason.CLIENT))
  }

  /* Request Monitoring */

  private fun initMonitor() {
    launch {
      do {
        val now = System.currentTimeMillis()
        packageRegistry
            .filter { entry -> entry.value <= now }
            .map { it.key }
            .forEach(this@ClientRequestController::handleBundleTimeout)
        delay(500)
      } while (true)
    }
  }

  private fun handleBundleTimeout(aPackage: Package) {
    // TODO: deregister aPackage from logChangeListener
    packageRegistry.remove(aPackage)
    aPackage.onError("error: timeout")
  }

  private fun handleBundleUnsupported(aPackage: Package) {
    // TODO: deregister aPackage from logChangeListener
    packageRegistry.remove(aPackage)
    aPackage.onError("error: operation not supported on this node")
  }

  private fun checkUnsupportedBundles(supportedOperations: Set<OperationCategory>) {
    packageRegistry
        .filter { e -> e.key.operationCategory !in supportedOperations }
        .forEach { (t, _) -> handleBundleUnsupported(t) }
  }

  companion object {
    val log = Logger.getLogger(ClientRequestController::class.java)
  }
}
