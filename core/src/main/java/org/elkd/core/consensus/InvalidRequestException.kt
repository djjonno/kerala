package org.elkd.core.consensus

import org.elkd.core.ElkdRuntimeException

class InvalidRequestException(override val message: String) : ElkdRuntimeException(message)