package org.elkd.core.consensus.replication

import org.elkd.core.consensus.LeaderContext
import org.elkd.core.consensus.Raft
import org.elkd.core.server.cluster.Node
import kotlin.coroutines.CoroutineContext

interface ReplicationControllerFactory {
  fun create(node: Node, leaderContext: LeaderContext, raft: Raft, coroutineContext: CoroutineContext): ReplicationController

  companion object {
    val DEFAULT: ReplicationControllerFactory = object : ReplicationControllerFactory {
      override fun create(node: Node, leaderContext: LeaderContext, raft: Raft, coroutineContext: CoroutineContext) =
          ReplicationController(node, leaderContext, raft)
    }
  }
}
