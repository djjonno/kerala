package org.elkd.core.server;

import org.apache.log4j.Logger;

public class Server {
  private static final Logger LOG = Logger.getLogger(Server.class);

  public Server() { }

  public void start(final int port) {
    LOG.info("starting server on :" + port);
  }

  public void shutdown() {
    LOG.info("shutting down server");
  }
}
