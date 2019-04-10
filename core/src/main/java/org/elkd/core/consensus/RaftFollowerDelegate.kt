package org.elkd.core.consensus

import com.google.common.annotations.VisibleForTesting
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
import kotlin.math.min

@Mockable
class RaftFollowerDelegate @VisibleForTesting
constructor(private val raft: Raft,
            private val timeoutMonitor: TimeoutMonitor) : RaftState {
  private val timeout = raft.config.getAsInteger(Config.KEY_RAFT_FOLLOWER_TIMEOUT_MS)

  constructor(raft: Raft) : this(
      raft,
      TimeoutMonitor { raft.transition(RaftCandidateDelegate::class.java) }
  )

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
      LOG.info(raft.log)
      validateAppendEntriesRequest(appendEntriesRequest)
      val appendFromCommand = AppendFromCommand
          .build(appendEntriesRequest.prevLogIndex + 1, appendEntriesRequest.entries, LogChangeReason.REPLICATION)
      raft.logCommandExecutor.execute(appendFromCommand)

      commitIfNecessary(appendEntriesRequest)
      replyAppendEntries(true, responseObserver)
    } catch (e: Exception) {
      LOG.error(e)
      replyAppendEntries(false, responseObserver)
    } finally {
      resetTimeout()
    }
  }

  private fun commitIfNecessary(appendEntriesRequest: AppendEntriesRequest) {
    if (appendEntriesRequest.leaderCommit > raft.log.commitIndex) {
      val commitIndex = min(appendEntriesRequest.leaderCommit, raft.log.lastIndex)
      raft.logCommandExecutor.execute(CommitCommand.build(commitIndex, LogChangeReason.REPLICATION))
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
        && raft.log.lastEntry.term <= requestVoteRequest.lastLogTerm) {
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
      throw Exception("Term mismatch. Validation Failed: requestTerm: ${appendEntriesRequest.term}, currentTerm:${raft.raftContext.currentTerm}")
    }

    val prevEntry = raft.log.read(appendEntriesRequest.prevLogIndex)
    if (prevEntry != null && prevEntry.term != appendEntriesRequest.prevLogTerm) {
      throw Exception("Entry.term mismatch. Validation Failed: prevLogIndex: ${appendEntriesRequest.prevLogIndex}, request: ${appendEntriesRequest.prevLogTerm}, prevEntry: ${prevEntry?.term}")
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
