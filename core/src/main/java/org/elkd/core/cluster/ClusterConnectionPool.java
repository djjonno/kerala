package org.elkd.core.cluster;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.elkd.core.server.ElkdClusterServiceGrpc;
import org.elkd.core.server.ElkdClusterServiceGrpc.ElkdClusterServiceFutureStub;

import java.util.HashSet;
import java.util.Set;

public class ClusterConnectionPool {
  private final Set<Channel> mChannels = new HashSet<>();

  public void initialize(final ClusterSet clusterSet) {
    for (final Node node : clusterSet.getNodes()) {
      final ManagedChannel channel = ManagedChannelBuilder
          .forAddress(node.getHost(), node.getPort())
          .usePlaintext()
          .build();
      mChannels.add(new Channel(channel));
    }
  }

  private class Channel {
    private ManagedChannel mManagedChannel;
    private ElkdClusterServiceFutureStub mStub;

    public Channel(final ManagedChannel managedChannel) {
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
