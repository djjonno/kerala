package org.kerala.core.runtime.client.command.parsers

import org.kerala.core.runtime.client.command.ClientCommand
import org.kerala.core.runtime.client.command.ClientCommandType
import org.kerala.core.server.client.RpcCommandRequest

class NoOpCommandParser : CommandParser() {
  override fun parse(type: ClientCommandType, request: RpcCommandRequest): ClientCommand = ClientCommand(mapOf())
  override fun rules() = emptyList<ValidationRule>()
}
