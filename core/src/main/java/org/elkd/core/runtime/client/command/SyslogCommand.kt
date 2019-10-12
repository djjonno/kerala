package org.elkd.core.runtime.client.command

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.KV

/**
 * SyslogCommand
 *
 * An object that encapsulates command literals, usually originating from a client,
 * which are destined for the @syslog Topic for replication and finally execution.
 */
open class SyslogCommand(val args: Map<String, String>) {

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

    inline fun builder(command: SyslogCommandType, commandBuilder: Builder.() -> Unit): SyslogCommand {
      val builder = Builder(command)
      builder.commandBuilder()
      return builder.build()
    }
  }

  class Builder(private val command: SyslogCommandType) {
    private val args = mutableMapOf<String, String>()

    fun arg(key: String, `val`: String) {
      args[key] = `val`
    }

    fun build(): SyslogCommand {
      args[KEY_COMMAND] = command.id
      return SyslogCommand(args)
    }
  }

  /**
   * Create Topic SyslogCommand
   *
   * Encapsulates parameters for provisioning a new Topic.
   */
  inner class CreateTopicSyslogCommand : SyslogCommand(args) {
    val id: String by args
    val namespace: String by args
  }

  inner class DeleteTopicSyslogCommand : SyslogCommand(args) {
    val namespace: String by args
  }

  /**
   * Leader Change SyslogCommand
   *
   * Encapsulates consensus change information.
   */
  inner class LeaderChangeSyslogCommand : SyslogCommand(args) {
    val leaderNode: String by args
  }
}

fun Entry.asCommand() : SyslogCommand {
  return SyslogCommand(this.kvs.map { it.key to it.`val` }.toMap())
}
