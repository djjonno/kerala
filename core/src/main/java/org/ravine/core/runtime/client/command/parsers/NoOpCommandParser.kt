package org.ravine.core.runtime.client.command.parsers

import org.ravine.core.runtime.client.command.ClientCommand
import org.ravine.core.runtime.client.command.ClientCommandType
import org.ravine.core.server.client.RpcCommandRequest

class NoOpCommandParser : CommandParser() {
  override fun parse(type: ClientCommandType, request: RpcCommandRequest): ClientCommand = ClientCommand(mapOf())
  override fun rules() = emptyList<ValidationRule>()
}
