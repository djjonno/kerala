package org.ravine.core.consensus.messages

data class RequestVoteResponse(
    val term: Int,
    val isVoteGranted: Boolean
)
