package org.elkd.core.server;

import java.util.logging.Logger;

public class ElkdServer {
  private static final Logger LOG = Logger.getLogger(ElkdServer.class.getName());

  private ElkdServer() { }

  public static void main(final String[] args) {
    LOG.info("running elkd");
  }
}
