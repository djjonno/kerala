package org.kerala.core.consensus.replication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import org.kerala.core.Environment
import org.kerala.core.concurrency.Pools
import org.kerala.core.concurrency.asCoroutineScope
import org.kerala.core.config.Config
import org.kerala.core.consensus.Raft
import org.kerala.core.runtime.topic.Topic
import org.kerala.core.runtime.topic.TopicRegistry.Listener

/**
 * Replicator schedules replication of the raft context over the cluster.
 */
class Replicator(private val raft: Raft) : CoroutineScope by Pools.replicationPool.asCoroutineScope() {
  private val broadcastInterval: Long = Environment.config[Config.KEY_RAFT_LEADER_BROADCAST_INTERVAL_MS]

  private val launcher = Launcher()

  fun launch() {
    raft.topicModule.topicRegistry.registerListener(launcher, Pools.replicationPool, rewind = true)
  }

  /**
   * Will kill all controllers.
   */
  fun shutdown() {
    coroutineContext.cancel()
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
      GroupedNodeReplicationController(raft, topic, raft.clusterSet, broadcastInterval, coroutineContext).also {
        jobs[topic] = it.launchController()
      }
    }

    private fun shutdown(topic: Topic) {
      jobs[topic]?.cancel()
    }
  }
}
