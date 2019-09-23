package org.elkd.core.consensus.states.follower

import com.google.common.annotations.VisibleForTesting
import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.consensus.InvalidRequestException
import org.elkd.core.consensus.OpCategory
import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.TimeoutAlarm
import org.elkd.core.consensus.messages.*
import org.elkd.core.consensus.states.RaftState
import org.elkd.core.consensus.states.State
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.AppendFromCommand
import org.elkd.core.log.commands.CommitCommand
import org.elkd.shared.annotations.Mockable
import org.elkd.shared.math.randomizeNumberPoint
import kotlin.math.min

@Mockable
class RaftFollowerState @VisibleForTesting
constructor(private val raft: Raft,
            @VisibleForTesting private val timeoutAlarm: TimeoutAlarm) : RaftState {
  private val timeout = raft.config.getAsInteger(Config.KEY_RAFT_FOLLOWER_TIMEOUT_MS)

  constructor(raft: Raft) : this(
      raft,
      TimeoutAlarm {
        logger.info("follower timeout reached.")
        raft.delegator.transition(State.CANDIDATE)
      }
  )

  override fun on() {
    resetTimeout()
  }

  override fun off() {
    stopTimeout()
  }

  override val supportedOps = setOf(OpCategory.CONSUME)

  override fun delegateAppendEntries(request: AppendEntriesRequest,
                                     stream: StreamObserver<AppendEntriesResponse>) {
    resetTimeout()
    try {
      logger.info("lastIndex: ${raft.log.lastIndex}, commitIndex: ${raft.log.commitIndex}")
      with (request) {
        validateAppendEntriesRequest(this)
        if (entries.size > 0) {
          logger.info("appending ${entries.size} entries")

          /**
           * Timeout Alarm is 'paused' here whilst the entries are appended to the logger,
           * as there could be many, we don't want to respond to the sender node until
           * the logs have been appended.  While performing this lengthy operation, it
           * it does not attribute to a leader node being absent.
           */
          stopTimeout()
          raft.logCommandExecutor.execute(AppendFromCommand
              .build(prevLogIndex + 1, entries, LogChangeReason.REPLICATION))
          resetTimeout()
        }
      }
      commitIfNecessary(request)
      replyAppendEntries(raft.raftContext, true, stream)

    } catch (e: Exception) {
      logger.error(e)
      replyAppendEntries(raft.raftContext, false, stream)
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
                                   stream: StreamObserver<RequestVoteResponse>) {
    resetTimeout()
    if (raft.raftContext.currentTerm > request.term ||
        raft.raftContext.votedFor !in listOf(null, request.candidateId) ||
        !isRequestLogLatest(request)) {
      replyRequestVote(raft.raftContext, false, stream)
      logger.info("no vote to ${request.candidateId}")
      return
    }

    with (raft.raftContext) {
      votedFor = request.candidateId
      currentTerm = request.term
    }
    replyRequestVote(raft.raftContext, true, stream)
    logger.info("yes vote to ${request.candidateId}")
  }

  private fun isRequestLogLatest(request: RequestVoteRequest): Boolean {
    return raft.log.lastIndex <= request.lastLogIndex
        && raft.log.lastEntry.term <= request.lastLogTerm
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
     * If no logger at previous index, we are missing an entry and this is invalid.
     */
    val prevEntry = raft.log.read(request.prevLogIndex) ?: throw InvalidRequestException("No Entry at prevLogIndex: ${request.prevLogIndex}")

    /*
     * If previous entry of request term does not match this logger previous logger entry term, invalid.
     */
    if (request.prevLogTerm != prevEntry.term) {
      throw InvalidRequestException("Entry.term mismatch. Validation Failed: prevLogIndex: ${request.prevLogIndex}, request: ${request.prevLogTerm}, prevEntry: ${prevEntry.term}")
    }
  }

  private fun stopTimeout() {
    timeoutAlarm.stop()
  }

  private fun resetTimeout() {
    val newTimeout = randomizeNumberPoint(timeout, 0.4)
    timeoutAlarm.reset(newTimeout.toLong())
  }

  companion object {
    private val logger = Logger.getLogger(RaftFollowerState::class.java.name)
  }
}
