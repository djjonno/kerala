package org.kerala.core.runtime.client.ctl.parsers

import org.kerala.core.runtime.client.ctl.CtlCommand
import org.kerala.core.runtime.client.ctl.CtlCommandType
import org.kerala.core.server.client.RpcCommandRequest

abstract class CommandParser {
  @Throws(Exception::class)
  open operator fun invoke(type: CtlCommandType, request: RpcCommandRequest): CtlCommand {
    return parse(type, request).also {
      validate(it)
    }
  }

  private fun validate(command: CtlCommand) {
    rules().forEach {
      if (!it.check(command)) {
        throw Exception(it.message(command))
      }
    }
  }

  abstract fun parse(type: CtlCommandType, request: RpcCommandRequest): CtlCommand
  abstract fun rules(): List<ValidationRule>
}

class DefaultCommandParser : CommandParser() {
  override fun parse(type: CtlCommandType, request: RpcCommandRequest): CtlCommand {
    return CtlCommand.builder(type) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
    }
  }

  override fun rules() = emptyList<ValidationRule>()
}
