@file:JvmName("Replies")
package org.elkd.core.consensus.messages

import io.grpc.stub.StreamObserver
import org.elkd.core.consensus.RaftContext

fun replyAppendEntries(raftContext: RaftContext, success: Boolean, observer: StreamObserver<AppendEntriesResponse>) {
  observer.onNext(AppendEntriesResponse.builder(raftContext.currentTerm, success).build())
  observer.onCompleted()
}

fun replyRequestVote(raftContext: RaftContext, success: Boolean, observer: StreamObserver<RequestVoteResponse>) {
  observer.onNext(RequestVoteResponse.builder(raftContext.currentTerm, success).build())
  observer.onCompleted()
}
