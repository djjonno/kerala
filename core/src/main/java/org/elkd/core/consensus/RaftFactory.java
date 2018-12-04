package org.elkd.core.consensus;

import org.elkd.core.config.Config;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.InMemoryLog;
import org.elkd.core.log.LogInvoker;
import org.elkd.core.server.cluster.ClusterSet;

public class RaftFactory {
  private RaftFactory() { }

  public static Raft create(final Config config, final ClusterSet clusterSet) {
    final LogInvoker<Entry> log = new LogInvoker<>(new InMemoryLog());
    return new Raft(
        config,
        clusterSet,
        new NodeProperties(log),
        new DefaultStateFactory()
    );
  }
}
