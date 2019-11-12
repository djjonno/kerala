package org.kerala.core.runtime.client.ctl.parsers

import org.kerala.core.runtime.client.ctl.CtlCommand
import org.kerala.core.runtime.client.ctl.CtlCommandType
import org.kerala.core.runtime.topic.Topic
import org.kerala.core.server.client.RpcCommandRequest

class CreateTopicCommandParser : CommandParser() {
  override fun parse(type: CtlCommandType, request: RpcCommandRequest): CtlCommand {
    return CtlCommand.builder(type) {
      request.argsList.forEach { pair ->
        arg(pair.arg, pair.param)
      }
      /* A topic requires a UUID */
      arg("id", Topic.generateId())
    }.CreateTopicCtlCommand()
  }

  override fun rules() = listOf(
      PropertyExists("id"),
      PropertyExists("namespace"),
      PropertyRegex("namespace", "^[0-9a-zA-Z_]{1,32}$")
  )
}
