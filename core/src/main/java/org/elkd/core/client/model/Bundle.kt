package org.elkd.core.client.model

interface Bundle {
  val requestId: String
  val opType: ClientOpType
  val onComplete: (message: String) -> Unit
  val onError: (message: String) -> Unit
  val timeout: Int
}
