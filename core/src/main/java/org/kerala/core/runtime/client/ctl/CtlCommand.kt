package org.kerala.core.runtime.client.ctl

import org.kerala.core.consensus.messages.Entry
import org.kerala.core.consensus.messages.KV

/**
 * CtlCommand
 *
 * An object that encapsulates command literals, usually originating from a ctl client,
 * which are destined for the @syslog Topic for replication and finally execution.
 */
open class CtlCommand(val args: Map<String, String>) {

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

    inline fun builder(command: CtlCommandType, commandBuilder: Builder.() -> Unit): CtlCommand {
      val builder = Builder(command)
      builder.commandBuilder()
      return builder.build()
    }
  }

  class Builder(private val command: CtlCommandType) {
    private val args = mutableMapOf<String, String>()

    fun arg(key: String, `val`: String) {
      args[key] = `val`
    }

    fun build(): CtlCommand {
      args[KEY_COMMAND] = command.id
      return CtlCommand(args)
    }
  }

  /**
   * Create Topic ClientCommand
   *
   * Encapsulates parameters for provisioning a new Topic.
   */
  inner class CreateTopicCtlCommand : CtlCommand(args) {
    val id: String by args
    val namespace: String by args
  }

  inner class DeleteTopicCtlCommand : CtlCommand(args) {
    val namespace: String by args
  }

  /**
   * Leader Change ClientCommand
   *
   * Encapsulates consensus change information.
   */
  inner class LeaderChangeCtlCommand : CtlCommand(args) {
    val leaderNode: String by args
  }
}

fun Entry.asCommand(): CtlCommand {
  return CtlCommand(this.kvs.map { it.key to it.`val` }.toMap())
}
