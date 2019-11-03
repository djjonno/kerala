@file:JvmName("Replies")
package org.ravine.core.consensus.messages

import io.grpc.stub.StreamObserver
import org.ravine.core.consensus.RaftContext

fun replyAppendEntries(raftContext: RaftContext, success: Boolean, observer: StreamObserver<AppendEntriesResponse>) {
  observer.onNext(AppendEntriesResponse(raftContext.currentTerm, success))
  observer.onCompleted()
}

fun replyRequestVote(raftContext: RaftContext, success: Boolean, observer: StreamObserver<RequestVoteResponse>) {
  observer.onNext(RequestVoteResponse(raftContext.currentTerm, success))
  observer.onCompleted()
}
