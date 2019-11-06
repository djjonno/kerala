package org.kerala.core.consensus.messages

data class AppendEntriesResponse(
    val term: Int,
    val isSuccessful: Boolean,
    val prevLogIndex: Long = 0
)
