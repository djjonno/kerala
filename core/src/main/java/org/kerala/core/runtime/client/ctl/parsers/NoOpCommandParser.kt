package org.kerala.core.runtime.client.ctl.parsers

import org.kerala.core.runtime.client.ctl.CtlCommand
import org.kerala.core.runtime.client.ctl.CtlCommandType
import org.kerala.core.server.client.RpcCommandRequest

class NoOpCommandParser : CommandParser() {
  override fun parse(type: CtlCommandType, request: RpcCommandRequest): CtlCommand = CtlCommand(mapOf())
  override fun rules() = emptyList<ValidationRule>()
}
