package org.elkd.core;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.elkd.core.config.Config;
import org.elkd.core.config.ConfigProvider;
import org.elkd.core.server.ElkdServer;

public class Elkd {
  private static final Logger LOG = Logger.getLogger(Elkd.class);

  private final Config mConfig;
  private final ElkdServer mElkdServer;

  private Elkd(final Config config) {
    this(config, new ElkdServer());
  }

  @VisibleForTesting
  Elkd(final Config config,
       final ElkdServer elkdServer) {
    mConfig = Preconditions.checkNotNull(config, "config");
    mElkdServer = Preconditions.checkNotNull(elkdServer, "elkdServer");
  }

  void start() {
    LOG.info("booting");

    final int port = mConfig.getInteger(Config.KEY_SERVER_PORT);

    mElkdServer.start(port);
  }

  void shutdown() {
    LOG.info("shutdown");
    mElkdServer.shutdown();
  }

  public static void main(final String[] args) {
    /* bootstrap */
    final Elkd elkd = new Elkd(ConfigProvider.getConfig());
    elkd.start();
    Runtime.getRuntime().addShutdownHook(new Thread(elkd::shutdown));
  }
}
