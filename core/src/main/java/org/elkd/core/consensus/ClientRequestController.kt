package org.elkd.core.consensus

import org.apache.log4j.Logger
import org.elkd.core.client.ClientModule
import org.elkd.core.client.TopicRegistry
import org.elkd.core.client.command.ClientCommands
import org.elkd.core.client.model.Bundle
import org.elkd.core.client.model.CommandBundle
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.KV
import org.elkd.core.log.LogChangeEvent
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.AppendCommand
import java.util.*

class ClientRequestController(val raft: Raft,
                              val clientModule: ClientModule) {

  private val queue: ArrayDeque<Bundle> = ArrayDeque()

//  fun replicate(topic: String, onComplete: () -> Unit) {
//    val entry = Entry.builder(raft.raftContext.currentTerm, topic).build()
//    raft.logComponentProvider.logChangeRegistry.register(entry, LogChangeEvent.COMMIT, onComplete)
//    raft.logComponentProvider.logCommandExecutor.execute(AppendCommand.build(entry, LogChangeReason.CLIENT))
//  }

  init {
    /* listen to raft state changes */
  }

  fun handleCommand(bundle: CommandBundle) {
    when (bundle.command) {
      ClientCommands.CREATE_TOPIC.id -> handleCreateTopic(bundle)
    }
  }

  private fun handleCreateTopic(bundle: CommandBundle) {
    if (clientModule.topicRegistry.hasTopic(bundle.command)) {
      bundle.onError("${bundle.command} already exists")

    } else {
      val entry = Entry.builder(raft.raftContext.currentTerm, TopicRegistry.SYSTEM_TOPIC)
          .addKV(KV(bundle.command, bundle.args.joinToString("&"))).build()

      registerBundleForEntry(bundle, entry)
    }
  }

  private fun registerBundleForEntry(bundle: Bundle, entry: Entry) {
    log.info("registering bundle for commit")

    queue.add(bundle)
    raft.logComponentProvider.logChangeRegistry.register(entry, LogChangeEvent.COMMIT, onComplete = {
      bundle.onComplete("OK")
    })
    raft.logComponentProvider.logCommandExecutor.execute(AppendCommand.build(entry, LogChangeReason.CLIENT))
  }

  companion object {
    val log = Logger.getLogger(ClientRequestController::class.java)
  }
}
