package org.elkd.core;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.elkd.core.config.Config;
import org.elkd.core.config.ConfigProvider;
import org.elkd.core.consensus.RaftDelegate;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.server.Server;
import org.elkd.core.server.converters.ConverterRegistry;

import java.io.IOException;

public class Elkd {
  private static final Logger LOG = Logger.getLogger(Elkd.class);

  private final Config mConfig;
  private final Server mServer;

  Elkd(final Config config,
       final Server server) {
    mConfig = Preconditions.checkNotNull(config, "config");
    mServer = Preconditions.checkNotNull(server, "server");
  }

  void start() throws IOException {
    LOG.info("booting");
    final int port = mConfig.getAsInteger(Config.KEY_SERVER_PORT);
    mServer.start(port);
  }

  void shutdown() {
    LOG.info("shutdown");
    mServer.shutdown();
  }

  void awaitTermination() throws InterruptedException {
    mServer.awaitTermination();
  }

  public static void main(final String[] args) {

    /* bootstrap */

    final Elkd elkd = new Elkd(ConfigProvider.getConfig(), new Server(new RaftDelegate() {

      /* temporary delegate for mocking */

      @Override
      public void delegateAppendEntries(final AppendEntriesRequest appendEntriesRequest, final StreamObserver<AppendEntriesResponse> responseObserver) {

      }

      @Override
      public void delegateRequestVote(final RequestVoteRequest requestVotesRequest, final StreamObserver<RequestVoteResponse> responseObserver) {

      }
    }, new ConverterRegistry()));

    Runtime.getRuntime().addShutdownHook(new Thread(elkd::shutdown));

    try {
      elkd.start();
      elkd.awaitTermination();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
