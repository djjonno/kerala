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
import org.elkd.core.consensus.messages.TopicTail
import org.elkd.core.consensus.messages.replyAppendEntries
import org.elkd.core.consensus.messages.replyRequestVote
import org.elkd.core.consensus.states.RaftState
import org.elkd.core.consensus.states.State
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.AppendFromCommand
import org.elkd.core.log.commands.CommitCommand
import org.elkd.core.runtime.topic.Topic
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
        raft.delegator.transitionRequest(State.CANDIDATE)
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
      with (request) {
        validateAppendEntriesRequest(this)
        val topic = raft.topicModule.topicRegistry.get(topicId)!!
        if (entries.isNotEmpty()) {
          /**
           * Timeout Alarm is 'paused' here whilst the entries are appended to the LOGGER,
           * as there could be many, we don't want to respond to the sender node until
           * the logs have been appended.  While performing this lengthy operation, it
           * it does not attribute to a leader node being absent.
           */
          stopTimeout()
          topic.logFacade.commandExecutor.execute(AppendFromCommand
              .build(prevLogIndex + 1, entries, LogChangeReason.REPLICATION))
          resetTimeout()
        }
        commitIfNecessary(topic, this)
        replyAppendEntries(raft.raftContext, true, stream)
      }
    } catch (e: Exception) {
      replyAppendEntries(raft.raftContext, false, stream)
    }
  }

  private fun commitIfNecessary(topic: Topic, request: AppendEntriesRequest) {
    with(request) {
      if (leaderCommit > topic.logFacade.log.commitIndex) {
        val commitIndex = min(leaderCommit, topic.logFacade.log.lastIndex)
        topic.logFacade.commandExecutor.execute(CommitCommand.build(commitIndex, LogChangeReason.REPLICATION))
      }
    }
  }

  override fun delegateRequestVote(request: RequestVoteRequest,
                                   stream: StreamObserver<RequestVoteResponse>) {
    resetTimeout()

    if (raft.raftContext.currentTerm <= request.term
        && raft.raftContext.votedFor in listOf(null, request.candidateId)
        && withLogTails(request.topicTails)) {

      raft.raftContext.votedFor = request.candidateId
      raft.raftContext.currentTerm = request.term
      replyRequestVote(raft.raftContext, true, stream)
    } else {
      replyRequestVote(raft.raftContext, false, stream)
    }
  }

  private fun withLogTails(topicTails: List<TopicTail>): Boolean {
    if (topicTails.size < raft.topicModule.topicRegistry.size) {
      return false
    }

    val results = topicTails.filter {
      it.topicId in raft.topicModule.topicRegistry
    }.map {
      it to raft.topicModule.topicRegistry.get(it.topicId)!!
    }.map {
      it.second.logFacade.log.lastIndex <= it.first.lastLogIndex &&
          it.second.logFacade.log.lastEntry.term <= it.first.lastLogTerm
    }

    return if (results.isEmpty()) {
      false
    } else {
      results.reduce { acc, b -> acc && b }
    }
  }

  @Throws(RaftException::class)
  private fun validateAppendEntriesRequest(request: AppendEntriesRequest) {
    /*
     * Does topic exist?
     */
    val topic = raft.topicModule.topicRegistry.get(request.topicId) ?: throw RaftException("Topic ${request.topicId} does not exist")

    /*
     * Request term must equal to or greater than raftContext.currentTerm, invalid.
     */
    if (request.term < raft.raftContext.currentTerm) {
      throw RaftException("Term mismatch (requestTerm: ${request.term}, currentTerm:${raft.raftContext.currentTerm})")
    }

    /*
     * If no entry at previous index, we are missing an entry and this is invalid.
     */
    val prevEntry = topic.logFacade.log.read(request.prevLogIndex) ?: throw RaftException("No entry @ ${request.prevLogIndex}")

    /*
     * If previous entry of controller term does not match this LOGGER previous LOGGER entry term, invalid.
     */
    if (request.prevLogTerm != prevEntry.term) {
      throw RaftException("Entry.term mismatch (prevLogIndex: ${request.prevLogIndex}, controller: ${request.prevLogTerm}, prevEntry: ${prevEntry.term})")
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
