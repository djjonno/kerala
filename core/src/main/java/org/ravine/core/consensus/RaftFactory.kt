package org.ravine.core.consensus

import org.ravine.core.runtime.topic.TopicModule
import org.ravine.core.server.cluster.ClusterMessenger

object RaftFactory {
  fun create(
      config: org.ravine.core.config.Config,
      topicModule: TopicModule,
      clusterMessenger: ClusterMessenger
  ): Raft {
    return Raft(
        config,
        clusterMessenger,
        RaftContext(),
        topicModule
    )
  }
}
