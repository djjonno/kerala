package org.elkd.core.consensus

import com.google.common.base.Preconditions
import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import java.util.*

internal class RaftCandidateDelegate(raft: Raft) : RaftState {
  private val raft: Raft

  init {
    this.raft = Preconditions.checkNotNull(raft, "raft")
  }

  override fun on() {
    LOG.info("candidate ready")
    startElection()
  }

  override fun off() {
    LOG.info("candidate offline")
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

  }

  companion object {
    private val LOG = Logger.getLogger(RaftCandidateDelegate::class.java.name)
  }
}
