package org.elkd.core.consensus.states.follower

import com.google.common.annotations.VisibleForTesting
import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.consensus.OpCategory
import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.RaftException
import org.elkd.core.consensus.TimeoutAlarm
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.elkd.core.consensus.messages.replyAppendEntries
import org.elkd.core.consensus.messages.replyRequestVote
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
        LOGGER.info("follower timeout reached.")
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
      LOGGER.info("lastIndex: ${raft.log.lastIndex}, commitIndex: ${raft.log.commitIndex}")
      with (request) {
        validateAppendEntriesRequest(this)
        if (entries.isNotEmpty()) {
          LOGGER.info("appending ${entries.size} entries")

          /**
           * Timeout Alarm is 'paused' here whilst the entries are appended to the LOGGER,
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
      LOGGER.error(e)
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
      return
    }

    with (raft.raftContext) {
      votedFor = request.candidateId
      currentTerm = request.term
    }
    replyRequestVote(raft.raftContext, true, stream)
  }

  private fun isRequestLogLatest(request: RequestVoteRequest): Boolean {
    return raft.log.lastIndex <= request.lastLogIndex
        && raft.log.lastEntry.term <= request.lastLogTerm
  }

  @Throws(RaftException::class)
  private fun validateAppendEntriesRequest(request: AppendEntriesRequest) {
    /*
     * request term must equal to or greater than raftContext.currentTerm, invalid.
     */
    if (request.term < raft.raftContext.currentTerm) {
      throw RaftException("Term mismatch (requestTerm: ${request.term}, currentTerm:${raft.raftContext.currentTerm})")
    }

    /*
     * If no LOGGER at previous index, we are missing an entry and this is invalid.
     */
    val prevEntry = raft.log.read(request.prevLogIndex) ?: throw RaftException("No entry @ ${request.prevLogIndex}")

    /*
     * If previous entry of request term does not match this LOGGER previous LOGGER entry term, invalid.
     */
    if (request.prevLogTerm != prevEntry.term) {
      throw RaftException("Entry.term mismatch (prevLogIndex: ${request.prevLogIndex}, request: ${request.prevLogTerm}, prevEntry: ${prevEntry.term})")
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
    private val LOGGER = Logger.getLogger(RaftFollowerState::class.java.name)
  }
}
