package org.kerala.core.runtime.client.ctl

import org.kerala.core.consensus.OpCategory
import org.kerala.shared.client.ClientACK

sealed class CtlCommandExecutionException(override val message: String, val status: Int) : Exception(message)
class CtlCommandUnknownException(val command: String) : CtlCommandExecutionException("`$command` is not a supported command", ClientACK.Codes.ERROR.id)
class CtlCommandOperationException(opCategory: OpCategory) : CtlCommandExecutionException("current node state does not support $opCategory operations", ClientACK.Codes.ERROR.id)
