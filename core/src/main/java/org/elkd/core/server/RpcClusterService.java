package org.elkd.core.server;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.ElkdRuntimeException;
import org.elkd.core.consensus.RaftDelegate;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.RequestVotesRequest;
import org.elkd.core.server.messages.ConverterRegistry;
import org.elkd.core.server.messages.decorators.AppendEntriesResponseStreamDecorator;
import org.elkd.core.server.messages.decorators.RequestVotesResponseStreamDecorator;

import javax.annotation.Nonnull;

public class RpcClusterService extends ElkdServiceGrpc.ElkdServiceImplBase {
  private static final Logger LOG = Logger.getLogger(RpcClusterService.class);

  private final RaftDelegate mRaftDelegate;
  private final ConverterRegistry mConverterRegistry;

  /* package */ RpcClusterService(@Nonnull final RaftDelegate raftDelegate,
                                  @Nonnull final ConverterRegistry converterRegistry) {
    mRaftDelegate = Preconditions.checkNotNull(raftDelegate, "raftDelegate");
    mConverterRegistry = Preconditions.checkNotNull(converterRegistry, "converterRegistry");
  }

  /* Cluster I/O */

  @Override
  public void appendEntries(final RpcAppendEntriesRequest appendEntriesRequest,
                            final StreamObserver<RpcAppendEntriesResponse> responseObserver) {
    try {
      final AppendEntriesRequest request = mConverterRegistry.transform(AppendEntriesRequest.class, appendEntriesRequest);
      final AppendEntriesResponseStreamDecorator observer = new AppendEntriesResponseStreamDecorator(responseObserver, mConverterRegistry);

      mRaftDelegate.delegateAppendEntries(
          request,
          observer
      );
    } catch (final ElkdRuntimeException e) {
      responseObserver.onError(e);
      LOG.error(e);
    }
  }

  @Override
  public void requestVotes(final RpcRequestVotesRequest requestVotesRequest,
                           final StreamObserver<RpcRequestVotesResponse> responseObserver) {
    try {
      final RequestVotesRequest request = mConverterRegistry.transform(RequestVotesRequest.class, requestVotesRequest);
      final RequestVotesResponseStreamDecorator observer = new RequestVotesResponseStreamDecorator(responseObserver, mConverterRegistry);

      mRaftDelegate.delegateRequestVotes(
          request,
          observer
      );
    } catch (final ElkdRuntimeException e) {
      responseObserver.onError(e);
      LOG.error(e);
    }
  }
}
