package org.elkd.core.consensus.messages

data class RequestVoteRequest(
    override val term: Int,
    val candidateId: String,
    val topicTails: List<TopicTail>
) : Request

data class TopicTail(
    val topicId: String,
    val lastLogIndex: Long,
    val lastLogTerm: Int
)
