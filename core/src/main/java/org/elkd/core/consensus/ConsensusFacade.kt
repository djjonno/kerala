package org.elkd.core.consensus

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.KV
import org.elkd.core.log.LogChangeEvent
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.AppendCommand

class ConsensusFacade(private val raft: Raft) {
  val delegator: RaftDelegator
    get() = raft.delegator

  val supportedOps: Set<OpCategory>
    get() = raft.supportedOps

  fun initialize() = raft.initialize()

  fun append(topic: String, kvs: List<KV>, onCommit: () -> Unit) {
    val entry = Entry.builder(raft.raftContext.currentTerm, topic).addAllKV(kvs).build()
    raft.logComponentProvider.logChangeRegistry.register(entry, LogChangeEvent.COMMIT, onCommit)
    val command = AppendCommand.build(entry, LogChangeReason.CLIENT)
    raft.logComponentProvider.logCommandExecutor.execute(command)
  }
}
