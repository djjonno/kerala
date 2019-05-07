package org.elkd.core.server.cluster;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.log4j.Logger;
import org.elkd.core.server.cluster.ElkdClusterServiceGrpc.ElkdClusterServiceFutureStub;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ClusterConnectionPool {
  private static final Logger LOG = Logger.getLogger(ClusterConnectionPool.class);

  private final Map<Node, Channel> mChannelMap = new HashMap<>();
  private final ClusterSet mClusterSet;

  public ClusterConnectionPool(final ClusterSet clusterSet) {
    mClusterSet = Preconditions.checkNotNull(clusterSet, "clusterSet");
  }

  public ClusterSet getClusterSet() {
    return mClusterSet;
  }

  public void initialize() {
    for (final Node node : mClusterSet.getNodes()) {
      final ManagedChannel channel = ManagedChannelBuilder
          .forTarget(node.getHost() + ":" + node.getPort())
          .usePlaintext() /* TODO: add cert auth */
          .build();
      mChannelMap.put(node, new Channel(channel));
      LOG.info("Initializing channel to instance " + node + " state " + channel.getState(true));
    }
  }

  @Nullable
  public Channel getChannel(final Node node) {
    return mChannelMap.get(node);
  }

  public static class Channel {
    private ManagedChannel mManagedChannel;
    private ElkdClusterServiceFutureStub mStub;

    private Channel(final ManagedChannel managedChannel) {
      mManagedChannel = managedChannel;
      mStub = ElkdClusterServiceGrpc.newFutureStub(managedChannel);
    }

    public ListenableFuture<RpcAppendEntriesResponse> appendEntries(final RpcAppendEntriesRequest request) {
      return mStub.appendEntries(request);
    }

    public ListenableFuture<RpcRequestVoteResponse> requestVote(final RpcRequestVoteRequest request) {
      return mStub.requestVote(request);
    }
  }
}
