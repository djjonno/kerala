package org.elkd.core.system

import org.elkd.core.client.TopicRegistry
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.KV

class SystemCommand(val command: SystemCommands, val args: String) {
  fun asEntry(term: Int): Entry {
    return Entry.builder(term, TopicRegistry.SYSTEM_TOPIC_NAME)
        .addAllKV(listOf(
            KV(KEY_COMMAND, command.id),
            KV(KEY_ARGS, args)
        ))
        .build()
  }

  companion object {
    private const val KEY_COMMAND = "cmd"
    private const val KEY_ARGS = "args"

    inline fun builder(command: SystemCommands, commandBuilder: Builder.() -> Unit): SystemCommand {
      val builder = Builder(command)
      builder.commandBuilder()
      return builder.build()
    }
  }

  class Builder(private val command: SystemCommands) {
    private val args: MutableList<Pair<String, String>> = mutableListOf()

    fun arg(key: String, `val`: String) {
      args.add(Pair(key, `val`))
    }

    fun build() : SystemCommand {
      return SystemCommand(command, args.joinToString("&") {
        "${it.first}=${it.second}"
      })
    }
  }
}
