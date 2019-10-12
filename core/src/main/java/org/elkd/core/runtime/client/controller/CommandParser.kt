package org.elkd.core.runtime.client.controller

import org.elkd.core.runtime.topic.Topic
import org.elkd.core.server.client.RpcClientRequest

abstract class CommandParser {
  @Throws(Exception::class)
  open operator fun invoke(type: SyslogCommandType, request: RpcClientRequest): SyslogCommand {
    return parse(type, request).also {
      validate(it)
    }
  }

  private fun validate(command: SyslogCommand) {
    rules().forEach {
      if (!it.check(command)) {
        throw Exception(it.message(command))
      }
    }
  }

  abstract fun parse(type: SyslogCommandType, request: RpcClientRequest): SyslogCommand
  abstract fun rules(): List<ValidationRule>
}

class PropertyRegex(private val property: String,
                    private val regex: String): ValidationRule {
  override fun check(command: SyslogCommand): Boolean {
    val namespace = command.args[property]
    return if (namespace != null) {
      Regex(regex).matches(namespace)
    } else false
  }

  override fun message(command: SyslogCommand) = "$property `${command.args[property]}` must match $regex"
}

class CreateTopicParser : CommandParser() {
  override fun parse(type: SyslogCommandType, request: RpcClientRequest): SyslogCommand {
    return SyslogCommand.builder(type) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
      /* A topic requires a UUID */
      arg("id", Topic.generateId())
    }.CreateTopicSyslogCommand()
  }

  override fun rules() = listOf(
      PropertyExists("id"),
      PropertyExists("namespace"),
      PropertyRegex("namespace", "^[0-9a-zA-Z_]{1,32}$")
  )
}

class NoOpCommandParser : CommandParser() {
  override fun parse(type: SyslogCommandType, request: RpcClientRequest): SyslogCommand = SyslogCommand(mapOf())
  override fun rules() = emptyList<ValidationRule>()
}
