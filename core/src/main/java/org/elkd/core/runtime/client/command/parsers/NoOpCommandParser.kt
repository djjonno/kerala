package org.elkd.core.runtime.client.command.parsers

import org.elkd.core.runtime.client.command.SyslogCommand
import org.elkd.core.runtime.client.command.SyslogCommandType
import org.elkd.core.server.client.RpcClientRequest

class NoOpCommandParser : CommandParser() {
  override fun parse(type: SyslogCommandType, request: RpcClientRequest): SyslogCommand = SyslogCommand(mapOf())
  override fun rules() = emptyList<ValidationRule>()
}