package org.ravine.core.consensus

import io.grpc.stub.StreamObserver
import org.ravine.core.consensus.messages.AppendEntriesRequest
import org.ravine.core.consensus.messages.AppendEntriesResponse
import org.ravine.core.consensus.messages.RequestVoteRequest
import org.ravine.core.consensus.messages.RequestVoteResponse

interface RaftDelegate {
  fun delegateAppendEntries(request: AppendEntriesRequest, stream: StreamObserver<AppendEntriesResponse>)
  fun delegateRequestVote(request: RequestVoteRequest, stream: StreamObserver<RequestVoteResponse>)

  val supportedOps: Set<OpCategory>
}
