package org.elkd.core.cluster;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import org.elkd.core.cluster.ClusterConnectionPool.Channel;
import org.elkd.core.cluster.exceptions.NodeNotFoundException;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.server.RpcAppendEntriesRequest;
import org.elkd.core.server.RpcAppendEntriesResponse;
import org.elkd.core.server.RpcRequestVoteRequest;
import org.elkd.core.server.RpcRequestVoteResponse;
import org.elkd.core.server.converters.ConverterRegistry;
import org.elkd.core.server.converters.ListenableFutureConverterDecorator;

/**
 * Send a message to a node in a given ClusterConnectionPool.
 *
 * Handles message conversion between server & consensus types.
 */
public class ClusterMessenger {
  private final ClusterConnectionPool mClusterConnectionPool;
  private final ConverterRegistry mConverterRegistry;

  public ClusterMessenger(final ClusterConnectionPool clusterConnectionPool) {
    this(clusterConnectionPool, ConverterRegistry.getInstance());
  }

  @VisibleForTesting
  ClusterMessenger(final ClusterConnectionPool clusterConnectionPool,
                   final ConverterRegistry converterRegistry) {
    mClusterConnectionPool = Preconditions.checkNotNull(clusterConnectionPool, "clusterConnectionPool");
    mConverterRegistry = Preconditions.checkNotNull(converterRegistry, "converterRegistry");
  }

  public ListenableFuture<AppendEntriesResponse> appendEntries(final Node node, final AppendEntriesRequest request) {
    final Channel channel = getChannel(node);

    final RpcAppendEntriesRequest convertedRequest = mConverterRegistry.convert(request);
    final ListenableFuture<RpcAppendEntriesResponse> future = channel.appendEntries(convertedRequest);

    return new ListenableFutureConverterDecorator<>(future, mConverterRegistry);
  }

  public ListenableFuture<RequestVoteResponse> requestVote(final Node node, final RequestVoteRequest request) {
    final Channel channel = getChannel(node);

    final RpcRequestVoteRequest convertedRequest = mConverterRegistry.convert(request);
    final ListenableFuture<RpcRequestVoteResponse> future = channel.requestVote(convertedRequest);

    return new ListenableFutureConverterDecorator<>(future, mConverterRegistry);
  }

  private Channel getChannel(final Node node) {
    final Channel channel = mClusterConnectionPool.getChannel(node);
    if (channel == null) {
      throw new NodeNotFoundException();
    }
    return channel;
  }
}
