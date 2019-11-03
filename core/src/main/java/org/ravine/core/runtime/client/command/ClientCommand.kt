package org.ravine.core.runtime.client.command

import org.ravine.core.consensus.messages.Entry
import org.ravine.core.consensus.messages.KV

/**
 * ClientCommand
 *
 * An object that encapsulates command literals, usually originating from a client,
 * which are destined for the @syslog Topic for replication and finally execution.
 */
open class ClientCommand(val args: Map<String, String>) {

  val command: String by args

  val kvs by lazy {
    args.map { KV(it.key, it.value) }.toList()
  }

  fun asEntry(term: Int): Entry {
    return Entry.builder(term)
        .addAllKV(kvs)
        .build()
  }

  companion object {
    private const val KEY_COMMAND = "command"

    inline fun builder(command: ClientCommandType, commandBuilder: Builder.() -> Unit): ClientCommand {
      val builder = Builder(command)
      builder.commandBuilder()
      return builder.build()
    }
  }

  class Builder(private val command: ClientCommandType) {
    private val args = mutableMapOf<String, String>()

    fun arg(key: String, `val`: String) {
      args[key] = `val`
    }

    fun build(): ClientCommand {
      args[KEY_COMMAND] = command.id
      return ClientCommand(args)
    }
  }

  /**
   * Create Topic ClientCommand
   *
   * Encapsulates parameters for provisioning a new Topic.
   */
  inner class CreateTopicClientCommand : ClientCommand(args) {
    val id: String by args
    val namespace: String by args
  }

  inner class DeleteTopicClientCommand : ClientCommand(args) {
    val namespace: String by args
  }

  /**
   * Leader Change ClientCommand
   *
   * Encapsulates consensus change information.
   */
  inner class LeaderChangeClientCommand : ClientCommand(args) {
    val leaderNode: String by args
  }
}

fun Entry.asCommand(): ClientCommand {
  return ClientCommand(this.kvs.map { it.key to it.`val` }.toMap())
}
