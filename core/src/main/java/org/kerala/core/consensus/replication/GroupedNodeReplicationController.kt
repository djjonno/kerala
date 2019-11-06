package org.kerala.core.consensus.replication

import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.log4j.Logger
import org.kerala.core.concurrency.Pools
import org.kerala.core.concurrency.asCoroutineScope
import org.kerala.core.consensus.Raft
import org.kerala.core.consensus.states.leader.LeaderContext
import org.kerala.core.log.LogChangeReason
import org.kerala.core.log.commands.CommitCommand
import org.kerala.core.runtime.topic.Topic
import org.kerala.core.server.cluster.ClusterSet
import org.kerala.shared.util.findMajority

class GroupedNodeReplicationController(
    private val raft: Raft,
    private val topic: Topic,
    private val clusterSet: ClusterSet,
    private val broadcastInterval: Long,
    private val context: CoroutineContext
) : CoroutineScope by Pools.replicationPool.asCoroutineScope(context) {

  fun launchController() = launch {
    LOGGER.info("launching replication for $topic")
    val leaderContext = LeaderContext(clusterSet.nodes, topic.logFacade.log.lastIndex)

    clusterSet.nodes.forEach {
      launch {
        NodeReplicationController(raft, topic, it, leaderContext, broadcastInterval).apply {
          start()
        }
      }
    }

    monitorTopicReplication(topic, leaderContext)
  }

  /* Topic Replication Servicing */

  private suspend fun monitorTopicReplication(topic: Topic, leaderContext: LeaderContext) {
    while (true) {
      delay(max(broadcastInterval - measureTimeMillis {
        commitCheck(topic, leaderContext)
      }, 0))
    }
  }

  private fun commitCheck(topic: Topic, leaderContext: LeaderContext) {
    val states = raft.clusterSet.nodes.map(leaderContext::getMatchIndex) + topic.logFacade.log.lastIndex

    states.findMajority()?.apply {
      if (this > topic.logFacade.log.commitIndex && topic.logFacade.log.read(this)?.term == raft.raftContext.currentTerm) {
        LOGGER.info("committing index=$this")
        topic.logFacade.commandExecutor.execute(CommitCommand.build(this, LogChangeReason.REPLICATION))
      }
    }

    LOGGER.info("$topic replication $states ${if (states.distinct().size == 1) "synchronized" else "propagating"}")
  }

  companion object {
    private val LOGGER = Logger.getLogger(GroupedNodeReplicationController::class.java)
  }
}
