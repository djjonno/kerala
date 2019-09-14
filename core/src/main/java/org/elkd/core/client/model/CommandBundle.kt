package org.elkd.core.client.model

import org.elkd.core.consensus.OpCategory
import org.elkd.core.system.SystemCommand
import java.util.*

data class CommandBundle(val requestId: String = UUID.randomUUID().toString(),
                         val opCategory: OpCategory = OpCategory.COMMAND,
                         val onComplete: (message: String) -> Unit,
                         val onError: (message: String) -> Unit,
                         val timeout: Int = 3000,
                         val command: SystemCommand)
