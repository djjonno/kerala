package org.elkd.core.consensus.replication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.concurrency.asCoroutineScope
import org.elkd.core.config.Config
import org.elkd.core.consensus.Raft
import org.elkd.core.runtime.topic.Topic
import org.elkd.core.runtime.topic.TopicRegistry.Listener

/**
 * Replicator schedules replication of the raft context over the cluster.
 */
class Replicator(private val raft: Raft) : CoroutineScope by Pools.replicationPool.asCoroutineScope() {
  private val broadcastInterval = raft.config.getAsLong(Config.KEY_RAFT_LEADER_BROADCAST_INTERVAL_MS)

  private val launcher = Launcher()

  fun launch() {
    LOGGER.info("Launching replicator")
    raft.topicModule.topicRegistry.registerListener(launcher, Pools.replicationPool, rewind = true)
  }

  /**
   * Will kill all controllers.
   */
  fun shutdown() {
    coroutineContext.cancel()
    LOGGER.info("Replicator shutdown")
  }

  private inner class Launcher : Listener {
    private val jobs = mutableMapOf<Topic, Job>()

    override fun onChange(topic: Topic, event: Listener.Event) {
      when (event) {
        Listener.Event.ADDED -> launch(topic)
        Listener.Event.REMOVED -> shutdown(topic)
      }
    }

    private fun launch(topic: Topic) {
      // if controller already exists, kill it
      GroupedNodeReplicationController(raft, topic, raft.clusterSet, broadcastInterval, coroutineContext).also {
        jobs[topic] = launch { it.launchController() }
      }
    }

    private fun shutdown(topic: Topic) {
      jobs[topic]?.cancel()
    }
  }

  companion object {
    private val LOGGER = Logger.getLogger(Replicator::class.java)
  }
}
