package org.elkd.core.consensus

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.client.TopicRegistry
import org.elkd.core.client.model.ClientOpType
import org.elkd.core.consensus.messages.*
import org.elkd.core.consensus.replication.Replicator
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.commands.AppendCommand
import org.elkd.core.system.SystemCommands

class RaftLeaderState(private val raft: Raft) : RaftState {
  private var replicator: Replicator? = null

  override fun on() {
    /* for test sake, append a new entry to the log here so we have something to replicate */
    val leaderContext = LeaderContext(raft.clusterSet.nodes, raft.log.lastIndex)
    val command = AppendCommand.build(
        Entry.builder(raft.raftContext.currentTerm, TopicRegistry.SYSTEM_TOPIC)
            .addKV(KV(SystemCommands.LEADER_CHANGE.id, raft.clusterSet.localNode.id))
            .build(), LogChangeReason.REPLICATION)
    raft.logCommandExecutor.execute(command)

    replicator = Replicator(raft, leaderContext)
    replicator?.start()
  }

  override fun off() {
    /* Force-stop the replication process - we must honor the transition */
    replicator?.stop()
  }

  override val supportedOperations = setOf(ClientOpType.PRODUCE, ClientOpType.COMMAND, ClientOpType.CONSUME)

  override fun delegateAppendEntries(request: AppendEntriesRequest,
                                     stream: StreamObserver<AppendEntriesResponse>) {
    stream.onNext(AppendEntriesResponse.builder(raft.raftContext.currentTerm, false).build())
    stream.onCompleted()
  }

  override fun delegateRequestVote(request: RequestVoteRequest,
                                   stream: StreamObserver<RequestVoteResponse>) {
    /* If term > currentTerm, Raft will always transition to Follower state. model received
       here will only be term <= currentTerm so we can defer all logic to the consensus delegate.
     */
    stream.onNext(RequestVoteResponse.builder(raft.raftContext.currentTerm, false).build())
    stream.onCompleted()
  }

  companion object {
    private val LOG = Logger.getLogger(RaftLeaderState::class.java)
  }
}
