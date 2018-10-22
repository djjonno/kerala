package org.elkd.core.server;

import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.elkd.core.config.Config;
import org.elkd.core.config.ConfigProvider;

import java.io.IOException;

public class ElkdServer {
  private static final Logger LOG = Logger.getLogger(ElkdServer.class);

  private ElkdServer() { }

  public static void main(final String[] args) throws IOException {
    LOG.info("running elkd");

    System.out.println(ConfigProvider.getConfig());

    LOG.debug("compiled config: " + ConfigProvider.getConfig());
    for (String arg : args) {
      LOG.info(arg);
    }
  }
}
