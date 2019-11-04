package org.ravine.core.consensus.replication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import org.ravine.core.Environment
import org.ravine.core.concurrency.Pools
import org.ravine.core.concurrency.asCoroutineScope
import org.ravine.core.config.Config
import org.ravine.core.consensus.Raft
import org.ravine.core.runtime.topic.Topic
import org.ravine.core.runtime.topic.TopicRegistry.Listener

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
      // if command already exists, kill it
      GroupedNodeReplicationController(raft, topic, raft.clusterSet, broadcastInterval, coroutineContext).also {
        jobs[topic] = it.launchController()
      }
    }

    private fun shutdown(topic: Topic) {
      jobs[topic]?.cancel()
    }
  }
}
