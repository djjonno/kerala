package org.kerala.core.consensus.states.candidate

import io.grpc.stub.StreamObserver
import org.kerala.core.Environment
import org.kerala.core.config.Config
import org.kerala.core.consensus.OpCategory
import org.kerala.core.consensus.Raft
import org.kerala.core.consensus.TimeoutAlarm
import org.kerala.core.consensus.messages.AppendEntriesRequest
import org.kerala.core.consensus.messages.AppendEntriesResponse
import org.kerala.core.consensus.messages.RequestVoteRequest
import org.kerala.core.consensus.messages.RequestVoteResponse
import org.kerala.core.consensus.messages.TopicTail
import org.kerala.core.consensus.states.RaftState
import org.kerala.core.consensus.states.State
import org.kerala.core.consensus.states.candidate.election.ElectionScheduler
import org.kerala.shared.annotations.Mockable
import org.kerala.shared.logger

@Mockable
class RaftCandidateState(
    private val raft: Raft,
    private val timeoutAlarm: TimeoutAlarm
) : RaftState {
  private val timeout: ULong = Environment.config[Config.KEY_RAFT_ELECTION_TIMEOUT_MS]
  private var electionScheduler: ElectionScheduler? = null

  constructor(raft: Raft) : this(
      raft,
      TimeoutAlarm {
        logger { i("election timeout reached. restarting election.") }
        raft.delegator.transitionRequest(State.CANDIDATE)
      }
  )

  override fun on() {
    timeoutAlarm.reset(timeout)
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
}
