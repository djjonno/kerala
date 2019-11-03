package org.ravine.core.log.ds

data class LogSnapshot<E> (
    val prevLogTerm: Int,
    val prevLogIndex: Long,
    val commitIndex: Long,
    val entries: List<E>
)
