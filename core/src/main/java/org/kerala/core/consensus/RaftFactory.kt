package org.kerala.core.consensus

import org.kerala.core.runtime.topic.TopicModule
import org.kerala.core.server.cluster.ClusterMessenger

object RaftFactory {
  fun create(
      topicModule: TopicModule,
      clusterMessenger: ClusterMessenger
  ): Raft {
    return Raft(
        clusterMessenger,
        RaftContext(),
        topicModule
    )
  }
}
