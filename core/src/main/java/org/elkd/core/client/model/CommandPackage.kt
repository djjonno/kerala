package org.elkd.core.client.model

import org.elkd.core.system.SystemCommand
import java.util.*

data class CommandPackage(override val requestId: String = UUID.randomUUID().toString(),
                          override val operationCategory: OperationCategory = OperationCategory.COMMAND,
                          override val onComplete: (message: String) -> Unit,
                          override val onError: (message: String) -> Unit,
                          override val timeout: Int = 3000,
                          val command: SystemCommand) : Package
