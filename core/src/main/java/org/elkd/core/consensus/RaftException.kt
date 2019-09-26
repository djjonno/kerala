package org.elkd.core.consensus

import org.elkd.core.ElkdRuntimeException

class RaftException(override val message: String) : ElkdRuntimeException(message)
