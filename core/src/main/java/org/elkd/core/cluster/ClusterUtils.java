package org.elkd.core.cluster;

import org.elkd.core.config.Config;
import org.elkd.shared.schemes.URI;

public class ClusterUtils {
  private ClusterUtils() { }

  public static Node buildSelfNode(final Config config) {
    final URI uri = URI.parseURIString(config.get(Config.KEY_HOST) + ":" + config.getAsInteger(Config.KEY_PORT));
    return new Node(uri);
  }
}
