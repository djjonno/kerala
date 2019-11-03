package org.ravine.core.consensus.messages

data class AppendEntriesResponse(
    val term: Int,
    val isSuccessful: Boolean
)
