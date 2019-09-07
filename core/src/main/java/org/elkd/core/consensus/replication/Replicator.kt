package org.elkd.core.consensus.replication

import kotlinx.coroutines.*
import org.apache.log4j.Logger
import org.elkd.core.concurrency.Pools
import org.elkd.core.config.Config
import org.elkd.core.consensus.LeaderContext
import org.elkd.core.consensus.Raft
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.CommitCommand
import org.elkd.shared.util.findMajority
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.system.measureTimeMillis

/**
 * Replicator schedules replication of the raft context over the cluster.
 */
class Replicator @JvmOverloads constructor (
    private val raft: Raft,
    private val leaderContext: LeaderContext,
    private val replicationControllerFactory: ReplicationControllerFactory = ReplicationControllerFactory.DEFAULT) : CoroutineScope {
  private val job = Job()

  /**
   * There are a number of co-routines created in this module. To prevent leakage when the Replicator is no longer
   * needed, the consumer can simply cancel the parent job/context.
   */
  override val coroutineContext: CoroutineContext
    get() = job + Pools.replicationThreadPool

  fun start() {
    LOG.info("Initiating replication across ${raft.clusterSet}")

    /* launch replication workers to own replication for each specific target */
    raft.clusterSet.nodes.forEach {
      val worker = replicationControllerFactory.create(it, leaderContext, raft, coroutineContext)
      launch {
        worker.start()
      }
    }

    launch {
      updateReplicationProgress()
    }
  }

  private suspend fun updateReplicationProgress() {
    while (true) {
      delay(max(raft.config.getAsLong(Config.KEY_RAFT_LEADER_BROADCAST_INTERVAL_MS) - measureTimeMillis {
        commitCheck()
      }, 0))
    }
  }

  private fun commitCheck() {
    val list = raft.clusterSet.nodes.map {
      leaderContext.getMatchIndex(it)
    }.toList() + raft.log.lastIndex /* this node should be included */
    val majority = findMajority(list)?.toLong()
    majority?.apply {
      if (this > raft.log.commitIndex &&
          raft.log.read(this)?.term == raft.raftContext.currentTerm) {
        LOG.info("majority @ index:$this w/ matching term –> committing to $this")
        raft.logCommandExecutor.execute(CommitCommand.build(this, LogChangeReason.REPLICATION))
      }
    }
  }

  fun stop() {
    job.cancel()
  }

  companion object {
    private val LOG = Logger.getLogger(Replicator::class.java)
  }
}
