package org.elkd.core.server;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.ElkdRuntimeException;
import org.elkd.core.consensus.RaftDelegate;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.server.converters.ConverterRegistry;
import org.elkd.core.server.converters.ResponseConverterStreamDecorator;

import javax.annotation.Nonnull;

public class ClusterService extends ElkdClusterServiceGrpc.ElkdClusterServiceImplBase {
  private static final Logger LOG = Logger.getLogger(ClusterService.class);

  private final RaftDelegate mRaftDelegate;
  private final ConverterRegistry mConverterRegistry;

  /* package */ ClusterService(@Nonnull final RaftDelegate raftDelegate,
                               @Nonnull final ConverterRegistry converterRegistry) {
    mRaftDelegate = Preconditions.checkNotNull(raftDelegate, "raftDelegate");
    mConverterRegistry = Preconditions.checkNotNull(converterRegistry, "converterRegistry");

    LOG.info("service ready");
  }

  /* Cluster I/O */

  @Override
  public void appendEntries(final RpcAppendEntriesRequest appendEntriesRequest,
                            final StreamObserver<RpcAppendEntriesResponse> responseObserver) {
    try {
      final AppendEntriesRequest request = mConverterRegistry.convert(appendEntriesRequest);
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
  public void requestVote(final RpcRequestVoteRequest requestVotesRequest,
                          final StreamObserver<RpcRequestVoteResponse> responseObserver) {
    try {
      final RequestVoteRequest request = mConverterRegistry.convert(requestVotesRequest);
      final ResponseConverterStreamDecorator<RequestVoteResponse, RpcRequestVoteResponse> observer =
          new ResponseConverterStreamDecorator<>(responseObserver, mConverterRegistry);

      mRaftDelegate.delegateRequestVote(
          request,
          observer
      );
    } catch (final ElkdRuntimeException e) {
      responseObserver.onError(e);
      LOG.error(e);
    }
  }
}
