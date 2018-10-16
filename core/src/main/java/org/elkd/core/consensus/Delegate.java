package org.elkd.core.consensus;

import io.grpc.stub.StreamObserver;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVotesRequest;
import org.elkd.core.consensus.messages.RequestVotesResponse;

public interface Delegate extends State {
  AppendEntriesResponse delegateAppendEntries(AppendEntriesRequest request, StreamObserver<AppendEntriesResponse> response);
  RequestVotesResponse delegateRequestVotes(RequestVotesRequest request, StreamObserver<RequestVotesResponse> response);
}