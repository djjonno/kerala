package org.elkd.core.consensus;

import io.grpc.stub.StreamObserver;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;

public interface RaftDelegate {
  void delegateAppendEntries(AppendEntriesRequest appendEntriesRequest, StreamObserver<AppendEntriesResponse> responseObserver);
  void delegateRequestVote(RequestVoteRequest requestVoteRequest, StreamObserver<RequestVoteResponse> responseObserver);
}
