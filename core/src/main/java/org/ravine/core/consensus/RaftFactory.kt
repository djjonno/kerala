package org.ravine.core.consensus

import org.ravine.core.runtime.topic.TopicModule
import org.ravine.core.server.cluster.ClusterMessenger

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
