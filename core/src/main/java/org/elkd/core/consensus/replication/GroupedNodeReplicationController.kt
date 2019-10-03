package org.elkd.core.consensus.replication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.states.leader.LeaderContext
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.CommitCommand
import org.elkd.core.runtime.topic.Topic
import org.elkd.core.server.cluster.ClusterSet
import org.elkd.shared.util.findMajority
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.system.measureTimeMillis

class GroupedNodeReplicationController(private val raft: Raft,
                                       private val topic: Topic,
                                       private val clusterSet: ClusterSet,
                                       private val broadcastInterval: Long) : CoroutineScope {
  private val job = Job()
  override val coroutineContext: CoroutineContext
    get() = job + Pools.replicationThreadPool.asCoroutineDispatcher()

  fun launchController() {
    LOGGER.info("Launching replication for $topic")
    val leaderContext = LeaderContext(clusterSet.nodes, topic.logFacade.log.lastIndex)

    clusterSet.nodes.forEach {
      launch {
        NodeReplicationController(raft, topic, it, leaderContext, broadcastInterval).apply {
          start()
        }
      }
    }

    launch {
      monitorTopicReplication(topic, leaderContext)
    }
  }

  fun shutdown() {
    LOGGER.info("shutting down replication for $topic")
    coroutineContext.cancel()
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
    val synchronized = if (states.distinct().size == 1) "synchronized" else "propagating"
    LOGGER.info("$topic replication $states $synchronized")
    states.findMajority()?.apply {
      if (this > topic.logFacade.log.commitIndex &&
          topic.logFacade.log.read(this)?.term == raft.raftContext.currentTerm) {
        LOGGER.info("majority @ index:$this w/ matching term –> committing to $this")
        topic.logFacade.commandExecutor.execute(CommitCommand.build(this, LogChangeReason.REPLICATION))
      }
    }
  }

  companion object {
    private val LOGGER = Logger.getLogger(GroupedNodeReplicationController::class.java)
  }
}
