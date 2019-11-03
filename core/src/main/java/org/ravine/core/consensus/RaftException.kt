package org.ravine.core.consensus

import org.ravine.core.RavineRuntimeException

class RaftException(override val message: String) : RavineRuntimeException(message)
