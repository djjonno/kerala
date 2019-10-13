package org.elkd.core.runtime.client.command.parsers

import org.elkd.core.runtime.client.command.ClientCommand
import org.elkd.core.runtime.client.command.ClientCommandType
import org.elkd.core.server.client.RpcClientRequest

class NoOpCommandParser : CommandParser() {
  override fun parse(type: ClientCommandType, request: RpcClientRequest): ClientCommand = ClientCommand(mapOf())
  override fun rules() = emptyList<ValidationRule>()
}