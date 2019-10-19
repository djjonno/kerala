package org.elkd.core.consensus

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.KV
import org.elkd.core.log.LogChangeEvent
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.LogChangeRegistry
import org.elkd.core.log.LogChangeRegistry.CancellationReason
import org.elkd.core.log.commands.AppendCommand
import org.elkd.core.runtime.topic.Topic
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ConsensusFacade(private val raft: Raft) {

  val delegator: RaftDelegator
    get() = raft.delegator

  val supportedOperations: Set<OpCategory>
    get() = raft.supportedOps

  fun initialize() = raft.initialize()

  val raftContext = raft.raftContext

  fun writeToTopic(topic: Topic,
                   kvs: List<KV>,
                   onCommit: () -> Unit,
                   onFailure: (CancellationReason) -> Unit = {}): LogChangeRegistry<Entry>.CompletionHandler {
    val entry = Entry.builder(raft.raftContext.currentTerm).addAllKV(kvs).build()
    val handler = topic.logFacade.changeRegistry.register(entry, LogChangeEvent.COMMIT, onCommit, onFailure)
    val command = AppendCommand.build(entry, LogChangeReason.CLIENT)
    topic.logFacade.commandExecutor.execute(command)
    return handler
  }

  suspend fun writeToTopic(topic: Topic, kvs: List<KV>) {
    suspendCoroutine<Unit> { cont ->
      writeToTopic(topic, kvs, { cont.resume(Unit) }, { cont.resumeWithException(Exception()) })
    }
  }
}
