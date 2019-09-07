package org.elkd.core.client.model

data class ClientCommandRequest(val command: String, val args: Array<String>) : ClientRequest {
  override val operation = ClientOpType.COMMAND
}
