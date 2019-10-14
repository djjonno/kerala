package org.elkd.core.consensus.replication

import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.concurrency.asCoroutineScope
import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.states.leader.LeaderContext
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.CommitCommand
import org.elkd.core.runtime.topic.Topic
import org.elkd.core.server.cluster.ClusterSet
import org.elkd.shared.util.findMajority

class GroupedNodeReplicationController(
    private val raft: Raft,
    private val topic: Topic,
    private val clusterSet: ClusterSet,
    private val broadcastInterval: Long,
    private val context: CoroutineContext
) : CoroutineScope by Pools.replicationPool.asCoroutineScope(context) {

  fun launchController() = launch {
    LOGGER.info("Launching replication for $topic")
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
        LOGGER.info("majority @ $this, performing commit($this)")
        topic.logFacade.commandExecutor.execute(CommitCommand.build(this, LogChangeReason.REPLICATION))
      }
    }

    LOGGER.info("$topic replication $states ${if (states.distinct().size == 1) "synchronized" else "propagating"}")
  }

  companion object {
    private val LOGGER = Logger.getLogger(GroupedNodeReplicationController::class.java)
  }
}
