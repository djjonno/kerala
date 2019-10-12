package org.elkd.core.runtime.client.command

import org.elkd.core.consensus.OpCategory
import java.util.UUID

data class ClientSyslogCommandPack(val requestId: String = UUID.randomUUID().toString(),
                                   val opCategory: OpCategory = OpCategory.COMMAND,
                                   val onComplete: () -> Unit,
                                   val onError: (message: String) -> Unit,
                                   val timeout: Int = 3000,
                                   val command: SyslogCommand)
