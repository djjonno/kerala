package org.kerala.core.consensus.states.follower

import com.google.common.annotations.VisibleForTesting
import io.grpc.stub.StreamObserver
import org.kerala.core.Environment
import org.kerala.core.config.Config
import org.kerala.core.consensus.EntryTermMismatch
import org.kerala.core.consensus.NoPreviousEntryException
import org.kerala.core.consensus.ObsoleteTermException
import org.kerala.core.consensus.OpCategory
import org.kerala.core.consensus.Raft
import org.kerala.core.consensus.RaftException
import org.kerala.core.consensus.TimeoutAlarm
import org.kerala.core.consensus.UnknownTopicException
import org.kerala.core.consensus.messages.AppendEntriesRequest
import org.kerala.core.consensus.messages.AppendEntriesResponse
import org.kerala.core.consensus.messages.RequestVoteRequest
import org.kerala.core.consensus.messages.RequestVoteResponse
import org.kerala.core.consensus.messages.TopicTail
import org.kerala.core.consensus.messages.replyAppendEntries
import org.kerala.core.consensus.messages.replyRequestVote
import org.kerala.core.consensus.states.RaftState
import org.kerala.core.consensus.states.State
import org.kerala.core.log.LogChangeReason
import org.kerala.core.log.commands.AppendFromCommand
import org.kerala.core.log.commands.CommitCommand
import org.kerala.core.runtime.topic.Topic
import org.kerala.shared.logger
import org.kerala.shared.math.randomizeNumberPoint
import kotlin.math.min

class RaftFollowerState @VisibleForTesting
constructor(
    private val raft: Raft,
    @VisibleForTesting private val timeoutAlarm: org.kerala.core.consensus.TimeoutAlarm
) : RaftState {
  private val timeout: Int = Environment.config[Config.KEY_RAFT_FOLLOWER_TIMEOUT_MS]

  constructor(raft: Raft) : this(
      raft,
      TimeoutAlarm {
        logger("follower timeout reached.")
        raft.delegator.transitionRequest(State.CANDIDATE)
      }
  )

  override fun on() {
    resetTimeout()
  }

  override fun off() {
    stopTimeout()
  }

  override val supportedOps = setOf(OpCategory.READ)

  override fun delegateAppendEntries(
      request: AppendEntriesRequest,
      stream: StreamObserver<AppendEntriesResponse>
  ) {
    resetTimeout()
    try {
      with(request) {
        validateAppendEntriesRequest(this)
        val topic = raft.topicModule.topicRegistry.getById(topicId)!!
        if (entries.isNotEmpty()) {
          /**
           * Timeout Alarm is 'paused' here whilst the entries are appended to the log,
           * as there could be many, we don't want to respond to the sender node until
           * the logs have been appended.  While performing this lengthy category, it
           * it does not attribute to a leader node being absent.
           */
          stopTimeout()
          topic.logFacade.commandExecutor.execute(AppendFromCommand
              .build(prevLogIndex + 1, entries, LogChangeReason.REPLICATION))
          resetTimeout()
        }
        commitIfNecessary(topic, this)
        replyAppendEntries(raft.raftContext, true, topic.logFacade.log.lastIndex, stream)
      }
    } catch (e: RaftException) {
      /* Attempt to retrieve prevLogIndex, otherwise default to 0 */
      val prevLogIndex = raft.topicModule.topicRegistry.getById(request.topicId)?.logFacade?.log?.lastIndex ?: 0
      replyAppendEntries(raft.raftContext, false, prevLogIndex, stream)
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

  override fun delegateRequestVote(
      request: RequestVoteRequest,
      stream: StreamObserver<RequestVoteResponse>
  ) {
    resetTimeout()

    if (raft.raftContext.currentTerm <= request.term &&
        raft.raftContext.votedFor in listOf(null, request.candidateId) &&
        withLogTails(request.topicTails)) {

      raft.raftContext.votedFor = request.candidateId
      raft.raftContext.currentTerm = request.term
      replyRequestVote(raft.raftContext, true, stream)
      logger("yes vote to ${raft.raftContext.votedFor}")
    } else {
      replyRequestVote(raft.raftContext, false, stream)
      logger("no vote to ${raft.raftContext.votedFor}")
    }
  }

  private fun withLogTails(topicTails: List<TopicTail>): Boolean {
    if (topicTails.size < raft.topicModule.topicRegistry.size) {
      return false
    }

    val results = topicTails.filter {
      raft.topicModule.topicRegistry.getById(it.topicId) != null
    }.map {
      it to raft.topicModule.topicRegistry.getById(it.topicId)!!
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
    val topic = raft.topicModule.topicRegistry.getById(request.topicId) ?: throw UnknownTopicException()

    /*
     * Request term must equal to or greater than raftContext.currentTerm, invalid.
     */
    if (request.term < raft.raftContext.currentTerm) {
      throw ObsoleteTermException()
    }

    /*
     * If no entry at previous index, we are missing an entry and this is invalid.
     */
    val prevEntry = topic.logFacade.log.read(request.prevLogIndex) ?: throw NoPreviousEntryException()

    /*
     * If previous entry of command term does not match this logs' previous entry term, invalid.
     */
    if (request.prevLogTerm != prevEntry.term) {
      throw EntryTermMismatch()
    }
  }

  private fun stopTimeout() {
    timeoutAlarm.stop()
  }

  private fun resetTimeout() {
    val newTimeout = randomizeNumberPoint(timeout, 0.4).toULong()
    timeoutAlarm.reset(newTimeout)
  }
}
