package org.elkd.core.consensus.states.candidate

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.config.Config
import org.elkd.core.consensus.OpCategory
import org.elkd.core.consensus.Raft
import org.elkd.core.consensus.TimeoutAlarm
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.TopicTail
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.elkd.core.consensus.states.RaftState
import org.elkd.core.consensus.states.State
import org.elkd.core.consensus.states.candidate.election.ElectionScheduler
import org.elkd.shared.annotations.Mockable

@Mockable
class RaftCandidateState(private val raft: Raft,
                         private val timeoutAlarm: TimeoutAlarm) : RaftState {
  private val timeout = raft.config.getAsInteger(Config.KEY_RAFT_ELECTION_TIMEOUT_MS)
  private var electionScheduler: ElectionScheduler? = null

  constructor(raft: Raft) : this(
      raft,
      TimeoutAlarm {
        LOGGER.info("election timeout reached. restarting election.")
        raft.delegator.transition(State.CANDIDATE)
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

  override val supportedOps = emptySet<OpCategory>()

  override fun delegateAppendEntries(request: AppendEntriesRequest,
                                     stream: StreamObserver<AppendEntriesResponse>) {
    /* If term > currentTerm, Raft will always transition to Follower state. model received
       here will only be term <= currentTerm so we can defer all logic to the consensus delegate.
     */
    stream.onNext(AppendEntriesResponse(raft.raftContext.currentTerm, false))
    stream.onCompleted()
  }

  override fun delegateRequestVote(request: RequestVoteRequest,
                                   stream: StreamObserver<RequestVoteResponse>) {
    /* If term > currentTerm, Raft will always transition to Follower state. model received
       here will only be term <= currentTerm so we can defer all logic to the consensus delegate.
     */
    stream.onNext(RequestVoteResponse(raft.raftContext.currentTerm, false))
    stream.onCompleted()
  }

  private fun startElection() {
    raft.raftContext.currentTerm = raft.raftContext.currentTerm + 1
    raft.raftContext.votedFor = raft.clusterSet.localNode.id

    val request = createVoteRequest()
    electionScheduler = ElectionScheduler.create(
        request,
        { raft.delegator.transition(State.LEADER) },
        { raft.delegator.transition(State.FOLLOWER) },
        raft.clusterMessenger)
    electionScheduler?.schedule()
  }

  private fun stopElection() {
    electionScheduler?.finish()
  }

  private fun createVoteRequest(): RequestVoteRequest {
    return RequestVoteRequest(
        raft.raftContext.currentTerm,
        raft.clusterSet.localNode.id,
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
