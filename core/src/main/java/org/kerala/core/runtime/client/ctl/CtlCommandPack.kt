package org.kerala.core.runtime.client.ctl

import java.util.UUID
import org.kerala.core.consensus.OpCategory

data class CtlCommandPack(
    val requestId: String = UUID.randomUUID().toString(),
    val opCategory: OpCategory = OpCategory.WRITE,
    val onComplete: (message: String) -> Unit,
    val onError: (error: String) -> Unit,
    val timeout: Int = 3000,
    val command: CtlCommand
)
