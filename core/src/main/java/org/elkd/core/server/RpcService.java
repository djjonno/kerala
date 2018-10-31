package org.elkd.core.server;

import io.grpc.stub.StreamObserver;
import org.elkd.core.consensus.RaftDelegate;

public class RpcService extends ElkdServiceGrpc.ElkdServiceImplBase {

  public RpcService(final Class<? extends RaftDelegate> raftDelegate) {
  }

  /* Cluster Comms */

  @Override
  public void appendEntries(final AppendEntriesRequest request, final StreamObserver<AppendEntriesResponse> responseObserver) {
    super.appendEntries(request, responseObserver);
  }

  @Override
  public void requestVotes(final RequestVotesRequest request, final StreamObserver<RequestVotesResponse> responseObserver) {
    super.requestVotes(request, responseObserver);
  }

  /* Client Comms */

}
