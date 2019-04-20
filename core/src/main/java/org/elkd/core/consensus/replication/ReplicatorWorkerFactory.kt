package org.elkd.core.consensus.replication

import org.elkd.core.consensus.LeaderContext
import org.elkd.core.consensus.Raft
import org.elkd.core.server.cluster.Node
import org.elkd.shared.annotations.Mockable
import kotlin.coroutines.CoroutineContext

interface ReplicatorWorkerFactory {
  fun create(node: Node, leaderContext: LeaderContext, raft: Raft, coroutineContext: CoroutineContext): ReplicatorWorker

  companion object {
    val DEFAULT: ReplicatorWorkerFactory = object : ReplicatorWorkerFactory {
      override fun create(node: Node, leaderContext: LeaderContext, raft: Raft, coroutineContext: CoroutineContext) =
          ReplicatorWorker(node, leaderContext, raft)
    }
  }
}
