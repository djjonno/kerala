package org.elkd.core.raft

import io.grpc.stub.StreamObserver
import org.elkd.core.raft.messages.AppendEntriesRequest
import org.elkd.core.raft.messages.AppendEntriesResponse
import org.elkd.core.raft.messages.RequestVoteRequest
import org.elkd.core.raft.messages.RequestVoteResponse

interface RaftDelegate {
  fun delegateAppendEntries(appendEntriesRequest: AppendEntriesRequest, responseObserver: StreamObserver<AppendEntriesResponse>)
  fun delegateRequestVote(requestVoteRequest: RequestVoteRequest, responseObserver: StreamObserver<RequestVoteResponse>)
}
