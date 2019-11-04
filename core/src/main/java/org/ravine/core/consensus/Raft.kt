package org.ravine.core.consensus

import org.ravine.core.consensus.messages.Request
import org.ravine.core.consensus.states.RaftStateFactory
import org.ravine.core.consensus.states.State
import org.ravine.core.runtime.topic.TopicModule
import org.ravine.core.server.cluster.ClusterMessenger
import org.ravine.core.server.cluster.ClusterSet
import org.ravine.shared.annotations.Mockable

/**
 * Raft module performs consensus of the topicModule over the cluster, using the [ClusterMessenger]
 * as a means of communication. This module registers as a delegate to the [org.ravine.core.server.cluster.ClusterService]
 * and acts as a state machine between the various consensus states.
 *
 * @see [https://raft.github.io/raft.pdf](https://raft.github.io/raft.pdf)
 * @see RaftFollowerState
 * @see RaftCandidateState
 * @see RaftLeaderState
 */
@Mockable
class Raft
internal constructor(
    val clusterMessenger: ClusterMessenger,
    val raftContext: RaftContext,
    val topicModule: TopicModule
) {

  val clusterSet: ClusterSet
    get() = clusterMessenger.clusterSet

  val supportedOps: Set<OpCategory>
    get() = delegator.supportedOps

  val delegator: RaftDelegator = RaftDelegator(RaftStateFactory(this), listOf(
      object : TransitionContract {

        override fun isTransitionRequired(request: Request): Boolean {
          return request.term > raftContext.currentTerm
        }

        override val transitionTo: State
          get() = State.FOLLOWER

        override val transitionPreHook: (request: Request) -> Unit
          get() = { request ->
            raftContext.currentTerm = request.term
            raftContext.votedFor = null
          }

        override val transitionPostHook: (request: Request) -> Unit
          get() = { /* no op */ }
      }
  ))

  fun initialize() {
    delegator.initialize(State.FOLLOWER)
  }
}
