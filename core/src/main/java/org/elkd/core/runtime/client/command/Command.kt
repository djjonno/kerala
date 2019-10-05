package org.elkd.core.runtime.client.command

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.KV

/**
 * Command
 *
 * Object that can be executed by the CommandExecutor.
 */
open class Command(val args: Map<String, String>) {

  val command: String by args

  val kvs by lazy {
    args.map { KV(it.key, it.value) }.toList()
  }

  fun asEntry(term: Int): Entry {
    return Entry.builder(term)
        .addAllKV(kvs)
        .build()
  }

  /* Builder */

  companion object {
    private const val KEY_COMMAND = "command"

    inline fun builder(command: CommandType, commandBuilder: Builder.() -> Unit) : Command {
      return builder(command.id, commandBuilder)
    }

    inline fun builder(command: String, commandBuilder: Builder.() -> Unit): Command {
      val builder = Builder(command)
      builder.commandBuilder()
      return builder.build()
    }
  }

  class Builder(private val command: String) {
    private val args = mutableMapOf<String, String>()

    fun arg(key: String, `val`: String) {
      args[key] = `val`
    }

    fun build(): Command {
      args[KEY_COMMAND] = command
      return Command(args)
    }
  }

  /**
   * Create Topic Command
   *
   * Encapsulates parameters for provisioning a new Topic.
   */
  inner class CreateTopicCommand : Command(args) {
    val id: String by args
    val namespace: String by args
  }

  /**
   * Leader Change Command
   *
   * Encapsulates consensus change information.
   */
  inner class LeaderChangeCommand : Command(args) {
    val leaderNode: String by args
  }
}

fun Entry.asCommand() : Command {
  return Command(this.kvs.map { it.key to it.`val` }.toMap())
}
