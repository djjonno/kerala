package org.ravine.core.log.exceptions

import org.ravine.core.RavineRuntimeException

class NonSequentialAppendException(lastIndex: Long, from: Long) :
    RavineRuntimeException("Cannot append from index: $from, when lastIndex: $lastIndex")
