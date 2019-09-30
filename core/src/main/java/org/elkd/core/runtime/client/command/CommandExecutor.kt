package org.elkd.core.runtime.client.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.consensus.OpCategory
import org.elkd.core.runtime.NotificationCenter
import kotlin.coroutines.CoroutineContext

class CommandExecutor(private val consensusFacade: ConsensusFacade) : CoroutineScope {
  override val coroutineContext: CoroutineContext
    get() = Job() + Dispatchers.Default

  private val bundleRegistry: MutableMap<CommandBundle, Long> = mutableMapOf()

  init {
    /* listen to consensusFacade state changes */
    initMonitor()
    initListener()
  }

  private fun initListener() {
    NotificationCenter.sub(
        NotificationCenter.Channel.CONSENSUS_STATE_CHANGE,
        Pools.clientCommandThreadPool
    ) {
      checkUnsupportedBundles(consensusFacade.supportedOperations)
    }
  }

  fun receive(bundle: CommandBundle) {
    if (bundle.opCategory !in consensusFacade.supportedOperations) {
      handleBundleUnsupported(bundle)
      return
    }

    executeCommandBundle(bundle)
  }

  private fun executeCommandBundle(bundle: CommandBundle) {
    bundleRegistry[bundle] = System.currentTimeMillis() + bundle.timeout
    consensusFacade.append("@system", bundle.command.kvs) {
      bundleRegistry.remove(bundle)
      bundle.onComplete("ok")
    }
  }

  /* Request Monitoring */

  private fun initMonitor() {
    launch {
      do {
        val now = System.currentTimeMillis()
        bundleRegistry
            .filter { entry -> entry.value <= now }
            .map { it.key }
            .forEach(this@CommandExecutor::handleBundleTimeout)
        delay(500)
      } while (true)
    }
  }

  private fun handleBundleTimeout(bundle: CommandBundle) {
    // TODO: deregister bundle from logChangeListener
    bundleRegistry.remove(bundle)
    bundle.onError("error: timeout")
  }

  private fun handleBundleUnsupported(bundle: CommandBundle) {
    // TODO: deregister bundle from logChangeListener
    bundleRegistry.remove(bundle)
    bundle.onError("error: operation not supported on this node")
  }

  private fun checkUnsupportedBundles(supportedOps: Set<OpCategory>) {
    bundleRegistry
        .filter { e -> e.key.opCategory !in supportedOps }
        .forEach { (t, _) -> handleBundleUnsupported(t) }
  }

  companion object {
    val logger = Logger.getLogger(CommandExecutor::class.java)
  }
}
