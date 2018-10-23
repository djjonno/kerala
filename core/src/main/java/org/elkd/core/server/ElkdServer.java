package org.elkd.core.server;

import org.apache.log4j.Logger;

public class ElkdServer {
  private static final Logger LOG = Logger.getLogger(ElkdServer.class);

  public ElkdServer() { }

  public void start(final int port) {
    LOG.info("starting server on :" + port);
  }

  public void shutdown() {
    LOG.info("shutting down server");
  }
}
