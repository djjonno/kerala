package org.elkd.core.server;

import com.google.common.base.Preconditions;
import io.grpc.ServerBuilder;
import org.apache.log4j.Logger;
import org.elkd.core.consensus.RaftDelegate;
import org.elkd.core.server.converters.ConverterRegistry;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetAddress;

public class Server {
  private static final Logger LOG = Logger.getLogger(Server.class);

  private io.grpc.Server mRpcClusterServer;

  private final RaftDelegate mRaftDelegate;
  private final ConverterRegistry mConverterRegistry = ConverterRegistry.getInstance();

  public Server(@Nonnull final RaftDelegate raftDelegate) {
    mRaftDelegate = Preconditions.checkNotNull(raftDelegate, "raftDelegate");
  }

  public void start(final int port) throws IOException {
    mRpcClusterServer = ServerBuilder.forPort(port)
        .addService(new ClusterService(mRaftDelegate, mConverterRegistry))
        .build()
        .start();

    LOG.info("started server on " + InetAddress.getLoopbackAddress() + ":" + port);
  }

  public void shutdown() {
    LOG.info("stopping server");
    mRpcClusterServer.shutdown();
  }

  public void awaitTermination() throws InterruptedException {
    mRpcClusterServer.awaitTermination();
  }
}
