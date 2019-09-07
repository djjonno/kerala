package org.elkd.core.client.model

import java.util.*

data class CommandBundle(override val requestId: String = UUID.randomUUID().toString(),
                         override val opType: ClientOpType = ClientOpType.COMMAND,
                         override val onComplete: (message: String) -> Unit,
                         override val onError: (message: String) -> Unit,
                         override val timeout: Int = 3000,
                         val command: String,
                         val args: List<String>) : Bundle
