package org.kerala.core.runtime.client.ctl.parsers

import org.kerala.core.runtime.client.ctl.CtlCommand
import org.kerala.core.runtime.client.ctl.CtlCommandType
import org.kerala.core.server.client.RpcCommandRequest

class DeleteTopicCommandParser : CommandParser() {
  override fun parse(type: CtlCommandType, request: RpcCommandRequest): CtlCommand {
    return CtlCommand.builder(type) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
    }.DeleteTopicCtlCommand()
  }

  override fun rules() = listOf(
      PropertyExists("namespace"),
      PropertyRegex("namespace", "^[0-9a-zA-Z_]{1,32}$")
  )
}
