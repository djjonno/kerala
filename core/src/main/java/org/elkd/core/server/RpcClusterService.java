package org.elkd.core.server;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.ElkdRuntimeException;
import org.elkd.core.consensus.RaftDelegate;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVotesRequest;
import org.elkd.core.consensus.messages.RequestVotesResponse;
import org.elkd.core.server.messages.ConverterRegistry;
import org.elkd.core.server.messages.ResponseConverterStreamDecorator;

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
      final AppendEntriesRequest request = mConverterRegistry.transform(appendEntriesRequest);
      final ResponseConverterStreamDecorator<AppendEntriesResponse, RpcAppendEntriesResponse> observer =
          new ResponseConverterStreamDecorator<>(responseObserver, mConverterRegistry);

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
      final RequestVotesRequest request = mConverterRegistry.transform(requestVotesRequest);
      final ResponseConverterStreamDecorator<RequestVotesResponse, RpcRequestVotesResponse> observer =
          new ResponseConverterStreamDecorator<>(responseObserver, mConverterRegistry);

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
