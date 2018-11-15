package org.elkd.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.apache.log4j.Logger;
import org.elkd.core.cluster.Node;
import org.elkd.core.cluster.StaticClusterConfig;
import org.elkd.core.config.Config;
import org.elkd.core.config.ConfigProvider;
import org.elkd.core.consensus.DefaultStateFactory;
import org.elkd.core.consensus.NodeState;
import org.elkd.core.consensus.Raft;
import org.elkd.core.log.InMemoryLog;
import org.elkd.core.log.LogInvoker;
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

    final Raft raft = new Raft(
        new LogInvoker<>(new InMemoryLog()),
        new StaticClusterConfig(ImmutableSet.of(
            new Node("elkd://127.0.0.1:9191"),
            new Node("elkd://127.0.0.1:9192")
        )),
        new NodeState(),
        new DefaultStateFactory()
    );

    final Elkd elkd = new Elkd(ConfigProvider.getConfig(), new Server(raft, new ConverterRegistry()));
    raft.initialize();

    Runtime.getRuntime().addShutdownHook(new Thread(elkd::shutdown));

    try {
      elkd.start();
      elkd.awaitTermination();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
