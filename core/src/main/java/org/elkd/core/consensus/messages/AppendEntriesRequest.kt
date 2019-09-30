package org.elkd.core.consensus.messages

data class AppendEntriesRequest(
    override val term: Int,
    val topicId: String,
    val prevLogTerm: Int,
    val prevLogIndex: Long,
    val leaderId: String,
    val leaderCommit: Long,
    val entries: List<Entry> = emptyList()
) : Request
