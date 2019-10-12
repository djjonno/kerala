package org.elkd.core.runtime.client.command.parsers

import org.elkd.core.runtime.client.command.SyslogCommand
import org.elkd.core.runtime.client.command.SyslogCommandType
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

class DefaultCommandParser : CommandParser() {
  override fun parse(type: SyslogCommandType, request: RpcClientRequest): SyslogCommand {
    return SyslogCommand.builder(type) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
    }
  }

  override fun rules() = emptyList<ValidationRule>()
}
