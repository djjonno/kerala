package org.elkd.core.runtime.client.command.parsers

import org.elkd.core.runtime.client.command.SyslogCommand
import org.elkd.core.runtime.client.command.SyslogCommandType
import org.elkd.core.server.client.RpcClientRequest

class DeleteTopicCommandParser : CommandParser() {
  override fun parse(type: SyslogCommandType, request: RpcClientRequest): SyslogCommand {
    return SyslogCommand.builder(type) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
    }.DeleteTopicSyslogCommand()
  }

  override fun rules() = listOf(
      PropertyExists("namespace"),
      PropertyRegex("namespace", "^[0-9a-zA-Z_]{1,32}$")
  )
}
