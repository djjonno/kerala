package org.elkd.core.consensus

import com.google.common.annotations.VisibleForTesting
import com.google.common.base.Preconditions
import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.AppendFromCommand
import org.elkd.core.log.commands.CommitCommand
import org.elkd.shared.annotations.Mockable

import org.elkd.shared.math.randomizeNumberPoint

@Mockable
class RaftFollowerDelegate @VisibleForTesting
constructor(private val raft: Raft,
            private val timeoutMonitor: TimeoutMonitor) : RaftState {

  private val timeout: Int

  constructor(raft: Raft) : this(
      raft,
      TimeoutMonitor { raft.transitionToState(RaftCandidateDelegate::class.java) }
  )

  init {
    timeout = Preconditions.checkNotNull(this.raft.config.getAsInteger(Config.KEY_RAFT_FOLLOWER_TIMEOUT_MS))
  }

  override fun on() {
    LOG.info("follower ready")
    resetTimeout()
  }

  override fun off() {
    LOG.info("follower offline")
    timeoutMonitor.stop()
  }

  override fun delegateAppendEntries(appendEntriesRequest: AppendEntriesRequest,
                                     responseObserver: StreamObserver<AppendEntriesResponse>) {
    try {
      validateAppendEntriesRequest(appendEntriesRequest)
      val appendFromCommand = AppendFromCommand
          .build((appendEntriesRequest.prevLogIndex + 1).toLong(), appendEntriesRequest.entries, LogChangeReason.REPLICATION)
      raft.logCommandExecutor.execute(appendFromCommand)

      if (appendEntriesRequest.leaderCommit > raft.log.commitIndex) {
        val commitIndex = Math.min(appendEntriesRequest.leaderCommit.toLong(), raft.log.lastIndex)
        raft.logCommandExecutor.execute(CommitCommand.build(commitIndex, LogChangeReason.REPLICATION))
      }
      replyAppendEntries(true, responseObserver)
    } catch (e: Exception) {
      replyAppendEntries(false, responseObserver)
    } finally {
      resetTimeout()
    }
  }

  override fun delegateRequestVote(requestVoteRequest: RequestVoteRequest,
                                   responseObserver: StreamObserver<RequestVoteResponse>) {
    val raftContext = raft.raftContext
    val currentTerm = raftContext.currentTerm

    if (requestVoteRequest.term < currentTerm) {
      replyRequestVote(false, responseObserver)
      return
    }

    if (raftContext.votedFor in listOf(null, requestVoteRequest.candidateId)
        && raft.log.lastIndex <= requestVoteRequest.lastLogIndex
        && (raft.log.lastIndex == -1L || raft.log.read(raft.log.lastIndex).term <= requestVoteRequest.lastLogTerm)) {
      raftContext.votedFor = requestVoteRequest.candidateId
      raftContext.currentTerm = requestVoteRequest.term
      replyRequestVote(true, responseObserver)
      return
    }

    replyRequestVote(false, responseObserver)
    return
  }

  @Throws(Exception::class)
  private fun validateAppendEntriesRequest(appendEntriesRequest: AppendEntriesRequest) {
    if (appendEntriesRequest.term < raft.raftContext.currentTerm) {
      throw Exception("Term mismatch. Validation Failed.")
    }
    /* This check is only relevant when there are entries in the log.
       `prevLogIndex == -1` occurs for the first ever log entry.
     */
    if (appendEntriesRequest.prevLogIndex > -1) {
      val prevEntry = raft.log.read(appendEntriesRequest.prevLogIndex.toLong())
      if (prevEntry == null || prevEntry.term != appendEntriesRequest.prevLogTerm) {
        throw Exception("Entry.term mismatch. Validation Failed.")
      }
    }
  }

  private fun replyAppendEntries(response: Boolean, responseObserver: StreamObserver<AppendEntriesResponse>) {
    responseObserver.onNext(AppendEntriesResponse.builder(raft.raftContext.currentTerm, response).build())
    responseObserver.onCompleted()
  }

  private fun replyRequestVote(response: Boolean, responseObserver: StreamObserver<RequestVoteResponse>) {
    responseObserver.onNext(RequestVoteResponse.builder(raft.raftContext.currentTerm, response).build())
    responseObserver.onCompleted()
  }

  private fun resetTimeout() {
    val newTimeout = randomizeNumberPoint(timeout, 0.2)
    LOG.info("timeout in " + newTimeout + "ms")
    timeoutMonitor.reset(newTimeout.toLong())
  }

  companion object {
    private val LOG = Logger.getLogger(RaftFollowerDelegate::class.java.name)
  }
}
