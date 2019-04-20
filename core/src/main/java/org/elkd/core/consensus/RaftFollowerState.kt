package org.elkd.core.consensus

import com.google.common.annotations.VisibleForTesting
import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.consensus.messages.*
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.AppendFromCommand
import org.elkd.core.log.commands.CommitCommand
import org.elkd.shared.annotations.Mockable
import org.elkd.shared.math.randomizeNumberPoint
import kotlin.math.min

@Mockable
class RaftFollowerState @VisibleForTesting
constructor(private val raft: Raft,
            private val timeoutMonitor: TimeoutMonitor) : RaftState {
  private val timeout = raft.config.getAsInteger(Config.KEY_RAFT_FOLLOWER_TIMEOUT_MS)

  constructor(raft: Raft) : this(
      raft,
      TimeoutMonitor {
        raft.delegator.transition(State.CANDIDATE)
      }
  )

  override fun on() {
    resetTimeout()
  }

  override fun off() {
    timeoutMonitor.stop()
  }

  override fun delegateAppendEntries(request: AppendEntriesRequest,
                                     responseObserver: StreamObserver<AppendEntriesResponse>) {
    try {
      LOG.info(raft.log)
      with (request) {
        validateAppendEntriesRequest(this)
        if (entries.size > 0) {
          raft.logCommandExecutor.execute(AppendFromCommand.build(prevLogIndex + 1, entries, LogChangeReason.REPLICATION))
        }
      }
      commitIfNecessary(request)
      replyAppendEntries(raft.raftContext, true, responseObserver)

    } catch (e: Exception) {
      LOG.error(e)
      replyAppendEntries(raft.raftContext, false, responseObserver)

    } finally {
      resetTimeout()
    }
  }

  private fun commitIfNecessary(request: AppendEntriesRequest) {
    with(request) {
      if (leaderCommit > raft.log.commitIndex) {
        val commitIndex = min(leaderCommit, raft.log.lastIndex)
        raft.logCommandExecutor.execute(CommitCommand.build(commitIndex, LogChangeReason.REPLICATION))
      }
    }
  }

  override fun delegateRequestVote(request: RequestVoteRequest,
                                   responseObserver: StreamObserver<RequestVoteResponse>) {

    if (request.term < raft.raftContext.currentTerm) {
      replyRequestVote(raft.raftContext, false, responseObserver)
      return
    }

    if (raft.raftContext.votedFor in listOf(null, request.candidateId)
        && raft.log.lastIndex <= request.lastLogIndex
        && raft.log.lastEntry.term <= request.lastLogTerm) {
      with (raft.raftContext) {
        votedFor = request.candidateId
        currentTerm = request.term
      }
      replyRequestVote(raft.raftContext, true, responseObserver)
      return
    }

    replyRequestVote(raft.raftContext, false, responseObserver)
    return
  }

  @Throws(InvalidRequestException::class)
  private fun validateAppendEntriesRequest(request: AppendEntriesRequest) {
    /*
     * request term must equal to or greater than raftContext.currentTerm, invalid.
     */
    if (request.term < raft.raftContext.currentTerm) {
      throw InvalidRequestException("Term mismatch. Validation Failed: requestTerm: ${request.term}, currentTerm:${raft.raftContext.currentTerm}")
    }

    /*
     * If no log at previous index, we are missing an entry and this is invalid.
     */
    val prevEntry = raft.log.read(request.prevLogIndex) ?: throw InvalidRequestException("No Entry at prevLogIndex: ${request.prevLogIndex}")

    /*
     * If previous entry of request term does not match this log previous log entry term, invalid.
     */
    if (request.prevLogTerm != prevEntry.term) {
      throw InvalidRequestException("Entry.term mismatch. Validation Failed: prevLogIndex: ${request.prevLogIndex}, request: ${request.prevLogTerm}, prevEntry: ${prevEntry.term}")
    }
  }

  private fun resetTimeout() {
    val newTimeout = randomizeNumberPoint(timeout, 0.2)
    LOG.info("timeout in " + newTimeout + "ms")
    timeoutMonitor.reset(newTimeout.toLong())
  }

  companion object {
    private val LOG = Logger.getLogger(RaftFollowerState::class.java.name)
  }
}
