package org.ravine.core.server.cluster;

import org.ravine.core.config.Config;
import org.ravine.shared.schemes.URI;

public class ClusterUtils {
  private ClusterUtils() { }

  public static Node buildSelfNode(final Config config) {
    final URI uri = URI.parseURIString(config.get(Config.KEY_HOST) + ":" + config.getAsInteger(Config.KEY_PORT));
    return new Node(uri);
  }
}
