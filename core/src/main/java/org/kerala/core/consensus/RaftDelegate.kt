package org.kerala.core.consensus

import io.grpc.stub.StreamObserver
import org.kerala.core.consensus.messages.AppendEntriesRequest
import org.kerala.core.consensus.messages.AppendEntriesResponse
import org.kerala.core.consensus.messages.RequestVoteRequest
import org.kerala.core.consensus.messages.RequestVoteResponse

interface RaftDelegate {
  fun delegateAppendEntries(request: AppendEntriesRequest, stream: StreamObserver<AppendEntriesResponse>)
  fun delegateRequestVote(request: RequestVoteRequest, stream: StreamObserver<RequestVoteResponse>)

  val supportedOps: Set<OpCategory>
}
