package org.elkd.core.cluster;

import com.google.common.base.Preconditions;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.log4j.Logger;
import org.elkd.core.server.ElkdClusterServiceGrpc;
import org.elkd.core.server.ElkdClusterServiceGrpc.ElkdClusterServiceFutureStub;

import java.util.HashMap;
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
      LOG.info("init channel -> " + node);
      final ManagedChannel channel = ManagedChannelBuilder
          .forTarget(node.getURI().toString())
          .usePlaintext()
          .build();
      mChannelMap.put(node, new Channel(channel));
    }
  }

  private class Channel {
    private ManagedChannel mManagedChannel;
    private ElkdClusterServiceFutureStub mStub;

    private Channel(final ManagedChannel managedChannel) {
      mManagedChannel = managedChannel;
      mStub = ElkdClusterServiceGrpc.newFutureStub(managedChannel);
    }

    private ManagedChannel getManagedChannel() {
      return mManagedChannel;
    }

    private ElkdClusterServiceFutureStub getFutureStub() {
      return mStub;
    }
  }
}
