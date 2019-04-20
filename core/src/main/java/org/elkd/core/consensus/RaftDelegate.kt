package org.elkd.core.consensus

import io.grpc.stub.StreamObserver
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse

interface RaftDelegate {
  fun delegateAppendEntries(request: AppendEntriesRequest, stream: StreamObserver<AppendEntriesResponse>)
  fun delegateRequestVote(request: RequestVoteRequest, stream: StreamObserver<RequestVoteResponse>)
}
