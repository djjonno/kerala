package org.elkd.core;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.elkd.core.config.Config;
import org.elkd.core.config.ConfigProvider;
import org.elkd.core.server.Server;

public class Elkd {
  private static final Logger LOG = Logger.getLogger(Elkd.class);

  private final Config mConfig;
  private final Server mServer;

  private Elkd(final Config config) {
    this(config, new Server());
  }

  @VisibleForTesting
  Elkd(final Config config,
       final Server server) {
    mConfig = Preconditions.checkNotNull(config, "config");
    mServer = Preconditions.checkNotNull(server, "server");
  }

  void start() {
    LOG.info("booting");

    final int port = mConfig.getAsInteger(Config.KEY_SERVER_PORT);

    mServer.start(port);
  }

  void stop() {
    LOG.info("stop");
    mServer.shutdown();
  }

  public static void main(final String[] args) {
    /* bootstrap */
    final Elkd elkd = new Elkd(ConfigProvider.getConfig());
    elkd.start();
    Runtime.getRuntime().addShutdownHook(new Thread(elkd::stop));
  }
}
