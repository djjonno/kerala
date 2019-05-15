package org.elkd.core.consensus

import org.elkd.core.config.Config
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.LogProvider
import org.elkd.core.server.cluster.ClusterMessenger

object RaftFactory {
  fun create(config: Config,
             logProvider: LogProvider<Entry>,
             clusterMessenger: ClusterMessenger): Raft {
    return Raft(
        config,
        clusterMessenger,
        RaftContext(),
        logProvider
    )
  }
}
