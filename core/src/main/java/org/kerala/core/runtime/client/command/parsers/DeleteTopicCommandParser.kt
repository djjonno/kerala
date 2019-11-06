package org.kerala.core.runtime.client.command.parsers

import org.kerala.core.runtime.client.command.ClientCommand
import org.kerala.core.runtime.client.command.ClientCommandType
import org.kerala.core.server.client.RpcCommandRequest

class DeleteTopicCommandParser : CommandParser() {
  override fun parse(type: ClientCommandType, request: RpcCommandRequest): ClientCommand {
    return ClientCommand.builder(type) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
    }.DeleteTopicClientCommand()
  }

  override fun rules() = listOf(
      PropertyExists("namespace"),
      PropertyRegex("namespace", "^[0-9a-zA-Z_]{1,32}$")
  )
}
