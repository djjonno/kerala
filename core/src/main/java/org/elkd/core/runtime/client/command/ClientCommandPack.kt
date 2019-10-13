package org.elkd.core.runtime.client.command

import org.elkd.core.consensus.OpCategory
import java.util.UUID

data class ClientCommandPack(val requestId: String = UUID.randomUUID().toString(),
                             val opCategory: OpCategory = OpCategory.WRITE,
                             val onComplete: (message: String) -> Unit,
                             val onError: (error: String) -> Unit,
                             val timeout: Int = 3000,
                             val command: ClientCommand)
