package org.elkd.core.client.model

interface Package {
  val requestId: String
  val operationCategory: OperationCategory
  val onComplete: (message: String) -> Unit
  val onError: (message: String) -> Unit
  val timeout: Int
}
