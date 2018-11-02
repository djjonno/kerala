package org.elkd.core.consensus;

import io.grpc.stub.StreamObserver;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVotesRequest;
import org.elkd.core.consensus.messages.RequestVotesResponse;

public interface RaftDelegate {
  void delegateAppendEntries(AppendEntriesRequest appendEntriesRequest, StreamObserver<AppendEntriesResponse> responseObserver);
  void delegateRequestVotes(RequestVotesRequest requestVotesRequest, StreamObserver<RequestVotesResponse> responseObserver);
}
