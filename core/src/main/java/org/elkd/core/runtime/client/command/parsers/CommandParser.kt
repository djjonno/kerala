package org.elkd.core.runtime.client.command.parsers

import org.elkd.core.runtime.client.command.ClientCommand
import org.elkd.core.runtime.client.command.ClientCommandType
import org.elkd.core.server.client.RpcClientRequest

abstract class CommandParser {
  @Throws(Exception::class)
  open operator fun invoke(type: ClientCommandType, request: RpcClientRequest): ClientCommand {
    return parse(type, request).also {
      validate(it)
    }
  }

  private fun validate(command: ClientCommand) {
    rules().forEach {
      if (!it.check(command)) {
        throw Exception(it.message(command))
      }
    }
  }

  abstract fun parse(type: ClientCommandType, request: RpcClientRequest): ClientCommand
  abstract fun rules(): List<ValidationRule>
}

class DefaultCommandParser : CommandParser() {
  override fun parse(type: ClientCommandType, request: RpcClientRequest): ClientCommand {
    return ClientCommand.builder(type) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
    }
  }

  override fun rules() = emptyList<ValidationRule>()
}
