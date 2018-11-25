package org.elkd.core;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.elkd.core.cluster.ClusterSet;
import org.elkd.core.cluster.ClusterUtils;
import org.elkd.core.cluster.Node;
import org.elkd.core.cluster.StaticClusterSet;
import org.elkd.core.config.Config;
import org.elkd.core.config.ConfigProvider;
import org.elkd.core.consensus.DefaultStateFactory;
import org.elkd.core.consensus.NodeState;
import org.elkd.core.consensus.Raft;
import org.elkd.core.log.InMemoryLog;
import org.elkd.core.log.LogInvoker;
import org.elkd.core.server.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Elkd {
  private static final Logger LOG = Logger.getLogger(Elkd.class);

  private final Config mConfig;
  private final Raft mRaft;
  private final Server mServer;

  Elkd(final Config config,
       final Raft raft,
       final Server server) {
    mConfig = Preconditions.checkNotNull(config, "config");
    mRaft = Preconditions.checkNotNull(raft, "raft");
    mServer = Preconditions.checkNotNull(server, "server");
  }

  void start() throws IOException {
    final int port = mConfig.getAsInteger(Config.KEY_PORT);
    mServer.start(port);
    mRaft.initialize();
  }

  void shutdown() {
    LOG.info("shutdown");
    mServer.shutdown();
  }

  void awaitTermination() throws InterruptedException {
    mServer.awaitTermination();
  }

  public static void main(final String[] args) throws UnknownHostException {
    /* bootstrap */

    final Config config = getConfig(args);
    if (config == null) {
      return;
    }
    LOG.debug("booting with " + config);

    final Node selfNode = ClusterUtils.buildSelfNode(config);
    final ClusterSet clusterSet = StaticClusterSet.builder(selfNode)
        .withString(config.get(Config.KEY_CLUSTER_SET))
        .build();
    LOG.info(clusterSet);

    final Raft raft = new Raft(
        new LogInvoker<>(new InMemoryLog()),
        clusterSet,
        new NodeState(),
        new DefaultStateFactory()
    );
    final Elkd elkd = new Elkd(
        config,
        raft,
        new Server(raft)
    );

    try {
      Runtime.getRuntime().addShutdownHook(new Thread(elkd::shutdown));
      elkd.start();
      elkd.awaitTermination();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  private static Config getConfig(final String[] args) {
    final Config config;
    try {
      config = ConfigProvider.compileConfig(args);
    } catch (final Exception e) {
      final String message = e.getMessage();
      if (message != null) {
        System.out.println(message);
      }
      return null;
    }
    return config;
  }
}
