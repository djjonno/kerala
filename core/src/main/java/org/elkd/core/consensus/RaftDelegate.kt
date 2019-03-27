package org.elkd.core.consensus

import io.grpc.stub.StreamObserver
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse

interface RaftDelegate {
  fun delegateAppendEntries(appendEntriesRequest: AppendEntriesRequest, responseObserver: StreamObserver<AppendEntriesResponse>)
  fun delegateRequestVote(requestVoteRequest: RequestVoteRequest, responseObserver: StreamObserver<RequestVoteResponse>)
}
