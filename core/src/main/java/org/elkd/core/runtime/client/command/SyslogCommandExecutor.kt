package org.elkd.core.runtime.client.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.elkd.core.concurrency.Pools
import org.elkd.core.consensus.ConsensusFacade
import org.elkd.core.consensus.OpCategory
import org.elkd.core.runtime.NotificationCenter
import kotlin.coroutines.CoroutineContext

class SyslogCommandExecutor(private val consensusFacade: ConsensusFacade) : CoroutineScope {
  override val coroutineContext: CoroutineContext
    get() = Job() + Dispatchers.Default

  private val bundleRegistry: MutableSet<ClientSyslogCommandPack> = mutableSetOf()

  init {
    /* listen to consensus state changes */
    NotificationCenter.sub(
        NotificationCenter.Channel.CONSENSUS_CHANGE,
        Pools.clientCommandPool
    ) {
      cleanupUnsupportedBundles(consensusFacade.supportedOperations)
    }
  }

  fun execute(bundle: ClientSyslogCommandPack) {
    if (bundle.opCategory !in consensusFacade.supportedOperations) {
      handleBundleUnsupported(bundle)
      return
    }

    executeCommandBundle(bundle)
  }

  private fun executeCommandBundle(bundle: ClientSyslogCommandPack) {
    bundleRegistry.add(bundle)
    consensusFacade.appendToSyslog(bundle.command.kvs) {
      bundleRegistry.remove(bundle)
      bundle.onComplete()
    }
  }

  private fun handleBundleUnsupported(bundle: ClientSyslogCommandPack) {
    bundleRegistry.remove(bundle)
    bundle.onError("node op ${bundle.opCategory} not supported")
  }

  private fun cleanupUnsupportedBundles(supportedOps: Set<OpCategory>) {
    bundleRegistry
        .filter { e -> e.opCategory !in supportedOps }
        .forEach { b -> handleBundleUnsupported(b) }
  }
}
