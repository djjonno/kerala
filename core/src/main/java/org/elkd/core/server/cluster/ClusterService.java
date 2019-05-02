package org.elkd.core.server.cluster;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.consensus.RaftDelegate;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.server.converters.ConverterRegistry;
import org.elkd.core.server.converters.StreamConverterDecorator;

import javax.annotation.Nonnull;

public class ClusterService extends ElkdClusterServiceGrpc.ElkdClusterServiceImplBase {
  private static final Logger LOG = Logger.getLogger(ClusterService.class);

  private final RaftDelegate mRaftDelegate;
  private final ConverterRegistry mConverterRegistry;

  public ClusterService(@Nonnull final RaftDelegate raftDelegate,
                        @Nonnull final ConverterRegistry converterRegistry) {
    mRaftDelegate = Preconditions.checkNotNull(raftDelegate, "raftDelegate");
    mConverterRegistry = Preconditions.checkNotNull(converterRegistry, "converterRegistry");

    LOG.info("service ready to accept target connections");
  }

  /* Cluster I/O */

  @Override
  public void appendEntries(final RpcAppendEntriesRequest appendEntriesRequest,
                            final StreamObserver<RpcAppendEntriesResponse> responseObserver) {
    try {
      final AppendEntriesRequest request = mConverterRegistry.convert(appendEntriesRequest);
      final StreamConverterDecorator<AppendEntriesResponse, RpcAppendEntriesResponse> observer =
          new StreamConverterDecorator<>(responseObserver, mConverterRegistry);
      mRaftDelegate.delegateAppendEntries(
          request,
          observer
      );
    } catch (final Exception e) {
      LOG.error(e);
      responseObserver.onError(e);
      responseObserver.onCompleted();
    }
  }

  @Override
  public void requestVote(final RpcRequestVoteRequest requestVotesRequest,
                          final StreamObserver<RpcRequestVoteResponse> responseObserver) {
    try {
      final RequestVoteRequest request = mConverterRegistry.convert(requestVotesRequest);
      final StreamConverterDecorator<RequestVoteResponse, RpcRequestVoteResponse> observer =
          new StreamConverterDecorator<>(responseObserver, mConverterRegistry);
      mRaftDelegate.delegateRequestVote(
          request,
          observer
      );
    } catch (final Exception e) {
      LOG.error(e);
      responseObserver.onError(e);
      responseObserver.onCompleted();
    }
  }
}
