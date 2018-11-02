package org.elkd.core.server;

import com.google.common.base.Preconditions;
import io.grpc.ServerBuilder;
import org.apache.log4j.Logger;
import org.elkd.core.consensus.RaftDelegate;
import org.elkd.core.server.messages.ConverterRegistry;

import javax.annotation.Nonnull;
import java.io.IOException;

public class Server {
  private static final Logger LOG = Logger.getLogger(Server.class);
  private final ConverterRegistry mConverterRegistry;

  private io.grpc.Server mRpcClusterServer;

  private final RaftDelegate mRaftDelegate;

  public Server(@Nonnull final RaftDelegate raftDelegate,
                @Nonnull final ConverterRegistry converterRegistry) {
    mRaftDelegate = Preconditions.checkNotNull(raftDelegate, "raftDelegate");
    mConverterRegistry = Preconditions.checkNotNull(converterRegistry, "converterRegistry");
  }

  public void start(final int port) throws IOException {
    mRpcClusterServer = ServerBuilder.forPort(port)
        .addService(new RpcClusterService(mRaftDelegate, mConverterRegistry))
        .build()
        .start();

    LOG.info("started server on 0.0.0.0:" + port);
  }

  public void shutdown() {
    LOG.info("shutting down server");
    mRpcClusterServer.shutdown();
  }

  public void awaitTermination() throws InterruptedException {
    mRpcClusterServer.awaitTermination();
  }
}
