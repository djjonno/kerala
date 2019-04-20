package org.elkd.core.consensus;

import org.elkd.core.config.Config;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.LogProvider;
import org.elkd.core.server.cluster.ClusterMessenger;

public class RaftFactory {
  private RaftFactory() { }

  public static Raft create(final Config config,
                            final LogProvider<Entry> logProvider,
                            final ClusterMessenger clusterMessenger) {
    return new Raft(
        config,
        clusterMessenger,
        new RaftContext(),
        logProvider
    );
  }
}
