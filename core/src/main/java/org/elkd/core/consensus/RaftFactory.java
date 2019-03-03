package org.elkd.core.consensus;

import org.elkd.core.config.Config;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.InMemoryLog;
import org.elkd.core.log.LogCommandExecutor;
import org.elkd.core.log.LogInvoker;
import org.elkd.core.log.LogProvider;
import org.elkd.core.server.cluster.ClusterSet;

public class RaftFactory {
  private RaftFactory() { }

  public static Raft create(final Config config,
                            final LogProvider<Entry> logProvider,
                            final ClusterSet clusterSet) {
    return new Raft(
        config,
        clusterSet,
        new RaftContext(),
        new DefaultStateFactory(),
        logProvider
    );
  }
}
