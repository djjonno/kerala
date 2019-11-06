package org.kerala.core.log.exceptions

import org.kerala.core.KeralaRuntimeException


enum class Event {
  /**
   * TIMEOUT - registered event took too long to lapse, timing out.
   */
  TIMEOUT
}

class LogChangeException(val event: Event) : KeralaRuntimeException()
