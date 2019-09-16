package org.elkd.core.runtime

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.consensus.messages.KV
import org.elkd.core.runtime.topic.Topic

class SystemCommand(val command: SystemCommandType,
                    private val args: List<Pair<String, String>>) {

  val kvs by lazy {
    args.map { KV(it.first, it.second) }.toList()
  }

  fun asEntry(term: Int): Entry {
    val builder = Entry.builder(term, Topic.SYSTEM_TOPIC_NAMESPACE)
        .addKV(KV(KEY_COMMAND, command.id));
    args.forEach { pair ->
      builder.addKV(KV(pair.first, pair.second))
    }
    return builder.build()
  }

  companion object {
    private const val KEY_COMMAND = "cmd"

    inline fun builder(command: SystemCommandType, commandBuilder: Builder.() -> Unit): SystemCommand {
      val builder = Builder(command)
      builder.commandBuilder()
      return builder.build()
    }
  }

  class Builder(private val command: SystemCommandType) {
    private val args: MutableList<Pair<String, String>> = mutableListOf()

    fun arg(key: String, `val`: String) {
      args.add(Pair(key, `val`))
    }

    fun build(): SystemCommand {
      args.add(Pair(KEY_COMMAND, command.id))
      return SystemCommand(command, args)
    }
  }
}
