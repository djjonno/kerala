package org.elkd.core.runtime.client.command

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.KV

/**
 * Command
 *
 * Object that can be executed by the CommandExecutor
 */
class Command(val command: Type,
              private val args: List<Pair<String, String>>) {

  val kvs by lazy {
    args.map { KV(it.first, it.second) }.toList()
  }

  fun asEntry(term: Int): Entry {
    val builder = Entry.builder(term)
        .addKV(KV(KEY_COMMAND, command.id));
    args.forEach { pair ->
      builder.addKV(KV(pair.first, pair.second))
    }
    return builder.build()
  }

  companion object {
    private const val KEY_COMMAND = "cmd"

    inline fun builder(command: Type, commandBuilder: Builder.() -> Unit): Command {
      val builder = Builder(command)
      builder.commandBuilder()
      return builder.build()
    }
  }

  class Builder(private val commandType: Type) {
    private val args: MutableList<Pair<String, String>> = mutableListOf()

    fun arg(key: String, `val`: String) {
      args.add(Pair(key, `val`))
    }

    fun build(): Command {
      args.add(Pair(KEY_COMMAND, commandType.id))
      return Command(commandType, args)
    }
  }

  enum class Type(val id: String) {
    /**
     * This does not change the leader, this handlers exists when a leader is changed,
     * and down-stream consumers should be informed.  Of course you could also obtain
     * this state directly from the Raft module.
     *
     * @param node host:port of new leader.
     */
    LEADER_CHANGE("leader-change"),

    /**
     * Creates a new topic and configures it in the TopicRegistry.
     *
     * @param namespace unique name of topic.
     */
    CREATE_TOPIC("create-topic")

    ;

    companion object {
      /**
       * Commands are received from clients in string form, this helper method maps
       * a string to its enum counterpart.
       */
      fun fromString(string: String): Type {
        return values().first { it.id == string }
      }
    }
  }
}
