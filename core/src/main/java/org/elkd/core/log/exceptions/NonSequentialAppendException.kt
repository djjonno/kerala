package org.elkd.core.log.exceptions

import org.elkd.core.ElkdRuntimeException

class NonSequentialAppendException(private val lastIndex: Long, private val from: Long) :
    ElkdRuntimeException("Cannot append from index: $from, when lastIndex: $lastIndex")
