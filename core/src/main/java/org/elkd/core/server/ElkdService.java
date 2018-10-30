package org.elkd.core.server;

import io.grpc.stub.StreamObserver;

public class ElkdService extends ElkdServiceGrpc.ElkdServiceImplBase {
  @Override
  public void appendEntries(final AppendEntriesRequest request, final StreamObserver<AppendEntriesResponse> responseObserver) {
    super.appendEntries(request, responseObserver);
  }

  @Override
  public void requestVotes(final RequestVotesRequest request, final StreamObserver<RequestVotesResponse> responseObserver) {
    super.requestVotes(request, responseObserver);
  }
}
