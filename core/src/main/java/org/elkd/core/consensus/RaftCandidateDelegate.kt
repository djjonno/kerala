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
    LOG.info("ready")
  }

  override fun off() {
    LOG.info("offline")
  }

  override fun delegateAppendEntries(appendEntriesRequest: AppendEntriesRequest,
                                     responseObserver: StreamObserver<AppendEntriesResponse>) {
    responseObserver.onCompleted()
  }

  override fun delegateRequestVote(requestVoteRequest: RequestVoteRequest,
                                   responseObserver: StreamObserver<RequestVoteResponse>) {
    responseObserver.onCompleted()
  }

  companion object {
    private val LOG = Logger.getLogger(RaftCandidateDelegate::class.java.name)
  }
}
