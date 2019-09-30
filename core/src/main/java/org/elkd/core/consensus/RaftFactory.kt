package org.elkd.core.consensus

import org.elkd.core.config.Config
import org.elkd.core.runtime.TopicModule
import org.elkd.core.server.cluster.ClusterMessenger

object RaftFactory {
  fun create(config: Config,
             topicModule: TopicModule,
             clusterMessenger: ClusterMessenger): Raft {
    return Raft(
        config,
        clusterMessenger,
        RaftContext(),
        topicModule
    )
  }
}
