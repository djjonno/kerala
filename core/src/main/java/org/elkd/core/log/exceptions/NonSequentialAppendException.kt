package org.elkd.core.log.exceptions

import org.elkd.core.ElkdRuntimeException

class NonSequentialAppendException(lastIndex: Long, from: Long) :
    ElkdRuntimeException("Cannot append from index: $from, when lastIndex: $lastIndex")
