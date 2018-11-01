package org.elkd.core.server;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.ElkdRuntimeException;
import org.elkd.core.consensus.RaftDelegate;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.server.payload.AppendEntriesResponseStreamDecorator;
import org.elkd.core.server.payload.PayloadAdapterRegistry;

import javax.annotation.Nonnull;

public class RpcService extends ElkdServiceGrpc.ElkdServiceImplBase {
  private final Logger LOG = Logger.getLogger(RpcService.class);

  private final RaftDelegate mRaftDelegate;
  private final PayloadAdapterRegistry mPayloadAdapterRegistry;

  /* package */ RpcService(@Nonnull final RaftDelegate raftDelegate,
                           @Nonnull final PayloadAdapterRegistry payloadAdapterRegistry) {
    mRaftDelegate = Preconditions.checkNotNull(raftDelegate, "raftDelegate");
    mPayloadAdapterRegistry = Preconditions.checkNotNull(payloadAdapterRegistry, "payloadAdapterRegistry");
  }

  /* Cluster I/O */

  @Override
  public void appendEntries(final RpcAppendEntriesRequest request,
                            final StreamObserver<RpcAppendEntriesResponse> responseObserver) {
    try {
      final AppendEntriesRequest transformedRequest = mPayloadAdapterRegistry.transform(AppendEntriesRequest.class, request);
      final AppendEntriesResponseStreamDecorator decoratedObserver = new AppendEntriesResponseStreamDecorator(responseObserver, mPayloadAdapterRegistry);

      mRaftDelegate.delegateAppendEntries(
          transformedRequest,
          decoratedObserver
      );
    } catch (final ElkdRuntimeException e) {
      responseObserver.onError(e);
      LOG.error(e);
    }
  }

  @Override
  public void requestVotes(final RpcRequestVotesRequest request,
                           final StreamObserver<RpcRequestVotesResponse> responseObserver) {
    super.requestVotes(request, responseObserver);
  }

  /* Client I/O */

}
