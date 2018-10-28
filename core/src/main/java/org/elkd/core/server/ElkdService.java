package org.elkd.core.server;

import io.grpc.stub.StreamObserver;

public class ElkdService extends ElkdServiceGrpc.ElkdServiceImplBase {
  @Override
  public void appendEntries(AppendEntriesRequest request, StreamObserver<AppendEntriesResponse> responseObserver) {
    super.appendEntries(request, responseObserver);
  }

  @Override
  public void requestVotes(RequestVotesRequest request, StreamObserver<RequestVotesResponse> responseObserver) {
    super.requestVotes(request, responseObserver);
  }
}
