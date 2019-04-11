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
  private val timeout = raft.config.getAsInteger(Config.KEY_RAFT_ELECTION_TIMEOUT_MS)
  private var electionScheduler: ElectionScheduler? = null

  constructor(raft: Raft) : this(
      raft,
      TimeoutMonitor {
        LOG.info("election timeout reached. restarting election.")
        raft.transition(RaftCandidateDelegate::class.java)
      }
  )

  override fun on() {
    LOG.info("candidate ready")
    timeoutMonitor.reset(timeout.toLong())
    startElection()
  }

  override fun off() {
    LOG.info("candidate offline")
    timeoutMonitor.stop()
    stopElection()
  }

  override fun delegateAppendEntries(request: AppendEntriesRequest,
                                     responseObserver: StreamObserver<AppendEntriesResponse>) {
    /* If term > currentTerm, Raft will always transition to Follower state. messages received
       here will only be term <= currentTerm so we can defer all logic to the consensus delegate.
     */
    responseObserver.onNext(AppendEntriesResponse.builder(raft.raftContext.currentTerm, false).build())
    responseObserver.onCompleted()
  }

  override fun delegateRequestVote(request: RequestVoteRequest,
                                   responseObserver: StreamObserver<RequestVoteResponse>) {
    /* If term > currentTerm, Raft will always transition to Follower state. messages received
       here will only be term <= currentTerm so we can defer all logic to the consensus delegate.
     */
    responseObserver.onNext(RequestVoteResponse.builder(raft.raftContext.currentTerm, false).build())
    responseObserver.onCompleted()
  }

  private fun startElection() {
    raft.raftContext.currentTerm = raft.raftContext.currentTerm + 1
    raft.raftContext.votedFor = raft.clusterSet.localNode.id

    val request = createVoteRequest()
    electionScheduler = ElectionScheduler.create(
        request,
        Runnable { raft.transition(RaftLeaderDelegate::class.java) },
        Runnable { raft.transition(RaftFollowerDelegate::class.java) },
        raft.clusterMessenger)
    electionScheduler?.schedule()
  }

  private fun stopElection() {
    electionScheduler?.finish()
  }

  private fun createVoteRequest(): RequestVoteRequest {
    return RequestVoteRequest.builder(
        raft.raftContext.currentTerm,
        raft.clusterSet.localNode.id,
        raft.log.lastIndex,
        raft.log.lastEntry.term
    ).build()
  }

  companion object {
    private val LOG = Logger.getLogger(RaftCandidateDelegate::class.java.name)
  }
}
