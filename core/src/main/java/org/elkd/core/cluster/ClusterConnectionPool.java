package org.elkd.core.cluster;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.log4j.Logger;
import org.elkd.core.server.ElkdClusterServiceGrpc;
import org.elkd.core.server.ElkdClusterServiceGrpc.ElkdClusterServiceFutureStub;
import org.elkd.core.server.RpcAppendEntriesRequest;
import org.elkd.core.server.RpcAppendEntriesResponse;
import org.elkd.core.server.RpcRequestVoteRequest;
import org.elkd.core.server.RpcRequestVoteResponse;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClusterConnectionPool {
  private static final Logger LOG = Logger.getLogger(ClusterConnectionPool.class);

  private final Map<Node, Channel> mChannelMap = new HashMap<>();
  private final ClusterSet mClusterSet;

  public ClusterConnectionPool(final ClusterSet clusterSet) {
    mClusterSet = Preconditions.checkNotNull(clusterSet, "clusterSet");
  }

  public void initialize() {
    LOG.info("initializing cluster connections with " + mClusterSet.clusterSize() + " nodes");

    for (final Node node : mClusterSet.getNodes()) {
      final ManagedChannel channel = ManagedChannelBuilder
          .forTarget(node.getURI().toString())
          .usePlaintext()
          .build();
      mChannelMap.put(node, new Channel(channel));
      LOG.info("init channel -> " + node + " state " + channel.getState(true));
    }
  }

  @Nullable
  public Channel getChannel(final Node node) {
    return mChannelMap.get(node);
  }

  public Iterator<Node> iterator() {
    return mChannelMap.keySet().iterator();
  }

  public static class Channel {
    private ManagedChannel mManagedChannel;
    private ElkdClusterServiceFutureStub mStub;

    private Channel(final ManagedChannel managedChannel) {
      mManagedChannel = managedChannel;
      mStub = ElkdClusterServiceGrpc.newFutureStub(managedChannel);
    }

    public ManagedChannel getManagedChannel() {
      return mManagedChannel;
    }

    public ListenableFuture<RpcAppendEntriesResponse> appendEntries(final RpcAppendEntriesRequest request) {
      return mStub.appendEntries(request);
    }

    public ListenableFuture<RpcRequestVoteResponse> requestVote(final RpcRequestVoteRequest request) {
      return mStub.requestVote(request);
    }
  }
}
