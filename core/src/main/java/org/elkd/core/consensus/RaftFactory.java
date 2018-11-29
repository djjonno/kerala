package org.elkd.core.consensus;

import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.InMemoryLog;
import org.elkd.core.log.LogInvoker;
import org.elkd.core.server.cluster.ClusterSet;

public class RaftFactory {
  public static Raft create(final ClusterSet clusterSet) {
    final LogInvoker<Entry> log = new LogInvoker<>(new InMemoryLog());
    final Raft raft = new Raft(
        clusterSet,
        new NodeProperties(log),
        new DefaultStateFactory()
    );

    return raft;
  }
}
