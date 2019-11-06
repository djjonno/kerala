package org.kerala.core.log.exceptions

import org.kerala.core.KeralaRuntimeException

class NonSequentialAppendException(lastIndex: Long, from: Long) :
    KeralaRuntimeException("Cannot append from index: $from, when lastIndex: $lastIndex")
