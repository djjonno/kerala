package org.kerala.core.consensus

import org.kerala.core.KeralaRuntimeException

class RaftException(override val message: String) : KeralaRuntimeException(message)
