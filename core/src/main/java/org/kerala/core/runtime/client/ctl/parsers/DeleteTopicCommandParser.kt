package org.kerala.core.runtime.client.ctl.parsers

import org.kerala.core.runtime.client.ctl.CtlCommand
import org.kerala.core.runtime.client.ctl.CtlCommandType
import org.kerala.core.runtime.topic.Topic
import org.kerala.core.server.client.KeralaCommandRequest

class DeleteTopicCommandParser : CommandParser() {
  override fun parse(type: CtlCommandType, request: KeralaCommandRequest): CtlCommand {
    return CtlCommand.builder(type) {
      request.argsList.forEach { pair ->
        arg(pair.param, pair.arg)
      }
    }.DeleteTopicCtlCommand()
  }

  override fun rules() = listOf(
      PropertyExists("namespace"),
      PropertyRegex("namespace", Topic.TOPIC_NAMESPACE_REGEX)
  )
}
