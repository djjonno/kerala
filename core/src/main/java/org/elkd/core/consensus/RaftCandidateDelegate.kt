package org.elkd.core.consensus

import com.google.common.base.Preconditions
import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.elkd.core.server.cluster.ClusterMessenger
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class RaftCandidateDelegate(raft: Raft,
                                     timeoutMonitor: TimeoutMonitor) : RaftState {
  private val raft: Raft
  private val timeout: Long
  private val timeoutMonitor: TimeoutMonitor
  private var clusterMessenger: ClusterMessenger

  constructor(raft: Raft) : this(
      raft,
      TimeoutMonitor { raft.transitionToState(RaftFollowerDelegate::class.java) }
  )

  init {
    this.raft = Preconditions.checkNotNull(raft, "raft")
    this.timeoutMonitor = Preconditions.checkNotNull(timeoutMonitor, "timeoutMonitor")
    timeout = raft.config.getAsLong(Config.KEY_RAFT_CANDIDATE_TIMEOUT_MS)
    clusterMessenger = ClusterMessenger(raft.clusterConnectionPool)
  }

  override fun on() {
    LOG.info("candidate ready")
    timeoutMonitor.reset(timeout)
    startElection()
  }

  override fun off() {
    LOG.info("candidate offline")
    timeoutMonitor.stop()
  }

  override fun delegateAppendEntries(appendEntriesRequest: AppendEntriesRequest,
                                     responseObserver: StreamObserver<AppendEntriesResponse>) {
    responseObserver.onCompleted()
  }

  override fun delegateRequestVote(requestVoteRequest: RequestVoteRequest,
                                   responseObserver: StreamObserver<RequestVoteResponse>) {
    /* If term > currentTerm, Raft will always transition to Follower state. messages received
       here will only be term <= currentTerm so we can defer all logic to the raft delegate.
     */
    responseObserver.onNext(RequestVoteResponse.builder(raft.raftContext.currentTerm, false).build())
    responseObserver.onCompleted()
  }

  private fun startElection() {
    raft.raftContext.currentTerm = raft.raftContext.currentTerm + 1
    raft.raftContext.votedFor = raft.clusterSet.selfNode.id

    val executor = Executors.newSingleThreadExecutor()
    raft.clusterConnectionPool.iterator().forEach {
      val request = RequestVoteRequest.builder(
          raft.raftContext.currentTerm,
          raft.clusterSet.selfNode.id,
          raft.log.lastIndex,
          if (raft.log.lastIndex == -1L) -1 else raft.log.read(raft.log.lastIndex).term
      ).build()
      val future = clusterMessenger.requestVote(it, request)
      future.addListener(Runnable {
        try {
          LOG.info(future.get())
        } catch (e: Exception) {
          LOG.info("could not contact $it")
        }
      }, executor)
    }
  }

  companion object {
    private val LOG = Logger.getLogger(RaftCandidateDelegate::class.java.name)
  }
}
