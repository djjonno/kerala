package org.elkd.core.runtime.client.command

import org.elkd.core.runtime.topic.Topic
import org.elkd.core.server.client.RpcClientCommandRequest

abstract class CommandTransformer {
  @Throws(Exception::class)
  open operator fun invoke(type: CommandType, request: RpcClientCommandRequest): Command {
    return parse(type, request).also {
      validate(it)
    }
  }

  private fun validate(command: Command) {
    rules().forEach {
      if (!it.check(command)) {
        throw Exception(it.message(command))
      }
    }
  }

  abstract fun parse(type: CommandType, request: RpcClientCommandRequest): Command
  abstract fun rules(): List<ValidationRule>
}

class PropertyRegex(private val property: String,
                    private val regex: String): ValidationRule {
  override fun check(command: Command): Boolean {
    val namespace = command.args[property]
    return if (namespace != null) {
      Regex(regex).matches(namespace)
    } else false
  }

  override fun message(command: Command) = "$property `${command.args[property]}` must match $regex"
}

class CreateTopicTransformer : CommandTransformer() {
  override fun parse(type: CommandType, request: RpcClientCommandRequest): Command {
    return Command.builder(request.command) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
      /* A topic requires a UUID */
      arg("id", Topic.generateId())
    }.CreateTopicCommand()
  }

  override fun rules() = listOf(
      PropertyExists("id"),
      PropertyExists("namespace"),
      PropertyRegex("namespace", "^[0-9a-zA-Z_]{1,32}$")
  )
}

class NoOpCommandTransformer : CommandTransformer() {
  override operator fun invoke(type: CommandType, request: RpcClientCommandRequest)
      = throw Exception("Command `${request.command}` could not be parsed")

  override fun parse(type: CommandType, request: RpcClientCommandRequest): Command = Command(mapOf())
  override fun rules() = emptyList<ValidationRule>()
}
