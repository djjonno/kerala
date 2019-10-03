package org.elkd.core.consensus.replication

import kotlinx.coroutines.*
import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.config.Config
import org.elkd.core.consensus.Raft
import org.elkd.core.runtime.topic.Topic
import org.elkd.core.runtime.topic.TopicRegistry.Listener
import kotlin.coroutines.CoroutineContext

/**
 * Replicator schedules replication of the raft context over the cluster.
 */
class Replicator(private val raft: Raft) : CoroutineScope {
  private val job = Job()
  private val broadcastInterval = raft.config.getAsLong(Config.KEY_RAFT_LEADER_BROADCAST_INTERVAL_MS)

  /**
   * There are a number of co-routines created in this module. To prevent leakage
   * when the Replicator is no longer needed, the consumer can simply cancel the
   * parent job/context.
   */
  private val threadPool = Pools.replicationThreadPool
  override val coroutineContext: CoroutineContext
    get() = job + threadPool.asCoroutineDispatcher()

  private val launcher = Launcher()

  fun launch() {
    LOGGER.info("Launching replicator")
    raft.topicModule.topicRegistry.registerListener(launcher, threadPool, rewind = true)
  }

  /**
   * Will kill all controllers.
   */
  fun shutdown() {
    coroutineContext.cancel()
    launcher.shutdown()
  }

  private inner class Launcher : Listener {
    private val controllers = mutableMapOf<Topic, GroupedNodeReplicationController>()

    override fun onChange(topic: Topic, event: Listener.Event) {
      when (event) {
        Listener.Event.ADDED -> launch(topic)
        Listener.Event.REMOVED -> shutdown(topic)
      }
    }

    fun shutdown() {
      controllers.forEach { (_, u) -> u.shutdown() }
    }

    private fun launch(topic: Topic) {
      // if controller already exists, kill it
      controllers[topic]?.shutdown()
      controllers[topic] = GroupedNodeReplicationController(raft, topic, raft.clusterSet, broadcastInterval).also {
        launch { it.launchController() }
      }
    }

    private fun shutdown(topic: Topic) {
      controllers[topic]?.shutdown()
    }
  }

  companion object {
    private val LOGGER = Logger.getLogger(Replicator::class.java)
  }
}
