package org.elkd.core.consensus.messages

data class RequestVoteRequest(
    override val term: Int,
    val candidateId: String,
    val lastLogIndex: Long,
    val lastLogTerm: Int
) : Request
