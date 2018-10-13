package org.elkd.core.server;

import com.google.common.collect.ImmutableSet;
import org.elkd.core.cluster.StaticClusterConfig;
import org.elkd.core.consensus.Consensus;
import org.elkd.core.consensus.ConsensusContext;
import org.elkd.core.consensus.DefaultDelegateFactory;

import java.util.logging.Logger;

public class ElkdServer {
  private static final Logger LOG = Logger.getLogger(ElkdServer.class.getName());

  private ElkdServer() { }

  public static void main(final String[] args) {
    LOG.info("running elkd");

    final StaticClusterConfig clusterMembership = new StaticClusterConfig(ImmutableSet.of(
        "elkd://localhost:9000",
        "elkd://localhost:9001",
        "elkd://localhost:9002",
        "elkd://localhost:9003"
    ));

    final Consensus consensus = new Consensus(
        clusterMembership,
        new ConsensusContext(),
        new DefaultDelegateFactory()
    );

    consensus.initialize();
  }
}
