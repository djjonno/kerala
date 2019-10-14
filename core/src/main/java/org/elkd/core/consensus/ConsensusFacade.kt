package org.elkd.core.consensus

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.KV
import org.elkd.core.log.LogChangeEvent
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.AppendCommand
import org.elkd.core.runtime.topic.Topic
import org.elkd.shared.annotations.Mockable

@Mockable
class ConsensusFacade(private val raft: Raft) {
  val delegator: RaftDelegator
    get() = raft.delegator

  val supportedOperations: Set<OpCategory>
    get() = raft.supportedOps

  fun initialize() = raft.initialize()

  val raftContext = raft.raftContext

  fun writeToTopic(topic: Topic, kvs: List<KV>, onCommit: () -> Unit) {
    val entry = Entry.builder(raft.raftContext.currentTerm).addAllKV(kvs).build()
    topic.logFacade.changeRegistry.register(entry, LogChangeEvent.COMMIT, onCommit)
    val command = AppendCommand.build(entry, LogChangeReason.CLIENT)
    topic.logFacade.commandExecutor.execute(command)
  }
}
