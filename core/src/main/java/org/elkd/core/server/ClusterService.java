package org.elkd.core.server;

import com.google.common.annotations.VisibleForTesting;
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
import org.elkd.core.server.converters.StreamConverterDecorator;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClusterService extends ElkdClusterServiceGrpc.ElkdClusterServiceImplBase {
  private static final Logger LOG = Logger.getLogger(ClusterService.class);

  private final RaftDelegate mRaftDelegate;
  private final ConverterRegistry mConverterRegistry;
  private final ExecutorService mThreadPool; /* should be single-thread for serial exec. */

  /* package */ ClusterService(@Nonnull final RaftDelegate raftDelegate,
                               @Nonnull final ConverterRegistry converterRegistry) {
    this(raftDelegate, converterRegistry, Executors.newSingleThreadExecutor());
  }

  @VisibleForTesting
  ClusterService(@Nonnull final RaftDelegate raftDelegate,
                 @Nonnull final ConverterRegistry converterRegistry,
                 @Nonnull final ExecutorService executorService) {
    mRaftDelegate = Preconditions.checkNotNull(raftDelegate, "raftDelegate");
    mConverterRegistry = Preconditions.checkNotNull(converterRegistry, "converterRegistry");
    mThreadPool = Preconditions.checkNotNull(executorService, "executorService");

    LOG.info("service ready to accept node connections");
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
    } catch (final ElkdRuntimeException e) {
      LOG.error(e);
      responseObserver.onError(e);
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
    } catch (final ElkdRuntimeException e) {
      LOG.error(e);
      responseObserver.onError(e);
    }
  }
}
