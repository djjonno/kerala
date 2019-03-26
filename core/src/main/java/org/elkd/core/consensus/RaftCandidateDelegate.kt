package org.elkd.core.consensus

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.consensus.election.ElectionScheduler
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse

class RaftCandidateDelegate(private val raft: Raft,
                            private val timeoutMonitor: TimeoutMonitor) : RaftState {
  private val timeout: Long
  private var electionScheduler: ElectionScheduler? = null

  init {
    timeout = raft.config.getAsLong(Config.KEY_RAFT_ELECTION_TIMEOUT_MS)
  }

  constructor(raft: Raft) : this(
      raft,
      TimeoutMonitor {
        LOG.info("election timeout reached. restarting election.")
        raft.transitionToState(RaftCandidateDelegate::class.java)
      }
  )

  override fun on() {
    LOG.info("candidate ready")
    timeoutMonitor.reset(timeout)
    startElection()
  }

  override fun off() {
    LOG.info("candidate offline")
    timeoutMonitor.stop()
    stopElection()
  }

  override fun delegateAppendEntries(appendEntriesRequest: AppendEntriesRequest,
                                     responseObserver: StreamObserver<AppendEntriesResponse>) {
    /* If term > currentTerm, Raft will always transition to Follower state. messages received
       here will only be term <= currentTerm so we can defer all logic to the raft delegate.
     */
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

    val request = createVoteRequest()
    electionScheduler = ElectionScheduler.create(
        request,
        Runnable { raft.transitionToState(RaftLeaderDelegate::class.java) },
        Runnable { raft.transitionToState(RaftFollowerDelegate::class.java) },
        raft.clusterMessenger)
    electionScheduler?.schedule()
  }

  private fun stopElection() {
    electionScheduler?.finish()
  }

  private fun createVoteRequest(): RequestVoteRequest {
    return RequestVoteRequest.builder(
        raft.raftContext.currentTerm,
        raft.clusterSet.selfNode.id,
        raft.log.lastIndex,
        /* send -1 if log is empty */
        if (raft.log.lastIndex == -1L) -1 else raft.log.read(raft.log.lastIndex).term
    ).build()
  }

  companion object {
    private val LOG = Logger.getLogger(RaftCandidateDelegate::class.java.name)
  }
}
