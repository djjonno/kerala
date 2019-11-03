package org.ravine.core.consensus.states.candidate

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.ravine.core.consensus.OpCategory
import org.ravine.core.consensus.Raft
import org.ravine.core.consensus.messages.AppendEntriesRequest
import org.ravine.core.consensus.messages.AppendEntriesResponse
import org.ravine.core.consensus.messages.RequestVoteRequest
import org.ravine.core.consensus.messages.RequestVoteResponse
import org.ravine.core.consensus.messages.TopicTail
import org.ravine.core.consensus.states.RaftState
import org.ravine.core.consensus.states.State
import org.ravine.core.consensus.states.candidate.election.ElectionScheduler
import org.ravine.shared.annotations.Mockable

@Mockable
class RaftCandidateState(
    private val raft: Raft,
    private val timeoutAlarm: org.ravine.core.consensus.TimeoutAlarm
) : RaftState {
  private val timeout = raft.config.getAsInteger(org.ravine.core.config.Config.KEY_RAFT_ELECTION_TIMEOUT_MS)
  private var electionScheduler: ElectionScheduler? = null

  constructor(raft: Raft) : this(
      raft,
      org.ravine.core.consensus.TimeoutAlarm {
        LOGGER.info("election timeout reached. restarting election.")
        raft.delegator.transitionRequest(State.CANDIDATE)
      }
  )

  override fun on() {
    timeoutAlarm.reset(timeout.toLong())
    startElection()
  }

  override fun off() {
    timeoutAlarm.stop()
    stopElection()
  }

  override val supportedOps = setOf(OpCategory.READ)

  override fun delegateAppendEntries(
      request: AppendEntriesRequest,
      stream: StreamObserver<AppendEntriesResponse>
  ) {
    /* If term > currentTerm, Raft will always transitionRequest to Follower state. model received
       here will only be term <= currentTerm so we can defer all logic to the consensus delegate.
     */
    stream.onNext(AppendEntriesResponse(raft.raftContext.currentTerm, false))
    stream.onCompleted()
  }

  override fun delegateRequestVote(
      request: RequestVoteRequest,
      stream: StreamObserver<RequestVoteResponse>
  ) {
    /* If term > currentTerm, Raft will always transitionRequest to Follower state. model received
       here will only be term <= currentTerm so we can defer all logic to the consensus delegate.
     */
    stream.onNext(RequestVoteResponse(raft.raftContext.currentTerm, false))
    stream.onCompleted()
  }

  private fun startElection() {
    raft.raftContext.currentTerm = raft.raftContext.currentTerm + 1
    raft.raftContext.votedFor = raft.clusterSet.selfNode.id

    val request = createVoteRequest()
    electionScheduler = ElectionScheduler.create(
        request,
        { raft.delegator.transitionRequest(State.LEADER) },
        { raft.delegator.transitionRequest(State.FOLLOWER) },
        raft.clusterMessenger)
    electionScheduler?.schedule()
  }

  private fun stopElection() {
    electionScheduler?.finish()
  }

  private fun createVoteRequest(): RequestVoteRequest {
    return RequestVoteRequest(
        raft.raftContext.currentTerm,
        raft.clusterSet.selfNode.id,
        topicTails = raft.topicModule.topicRegistry.topics.map {
          TopicTail(
              topicId = it.id,
              lastLogIndex = it.logFacade.log.lastIndex,
              lastLogTerm = it.logFacade.log.lastEntry.term
          )
        }
    )
  }

  companion object {
    private val LOGGER = Logger.getLogger(RaftCandidateState::class.java.name)
  }
}
