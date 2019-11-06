package org.kerala.core.server.converters

import org.kerala.core.consensus.messages.RequestVoteRequest
import org.kerala.core.consensus.messages.RequestVoteResponse
import org.kerala.core.consensus.messages.TopicTail
import org.kerala.core.server.cluster.RpcLogTail
import org.kerala.core.server.cluster.RpcRequestVoteRequest
import org.kerala.core.server.cluster.RpcRequestVoteResponse

class RequestVoteConverters {
  class ToRpcRequest : Converter<RequestVoteRequest, RpcRequestVoteRequest> {
    override fun convert(source: RequestVoteRequest): RpcRequestVoteRequest {
      return RpcRequestVoteRequest.newBuilder()
          .setTerm(source.term)
          .setCandidateId(source.candidateId)
          .addAllLogTails(source.topicTails.map {
            RpcLogTail.newBuilder()
                .setTopicId(it.topicId)
                .setLastLogIndex(it.lastLogIndex)
                .setLastLogTerm(it.lastLogTerm)
                .build()
          })
          .build()
    }
  }

  class FromRpcRequest : Converter<RpcRequestVoteRequest, RequestVoteRequest> {
    override fun convert(source: RpcRequestVoteRequest): RequestVoteRequest {
      return RequestVoteRequest(
          term = source.term,
          candidateId = source.candidateId,
          topicTails = source.logTailsList.map {
            TopicTail(
                topicId = it.topicId,
                lastLogIndex = it.lastLogIndex,
                lastLogTerm = it.lastLogTerm
            )
          }
      )
    }
  }

  class ToRpcResponse : Converter<RequestVoteResponse, RpcRequestVoteResponse> {
    override fun convert(source: RequestVoteResponse): RpcRequestVoteResponse {
      return RpcRequestVoteResponse.newBuilder()
          .setTerm(source.term)
          .setVoteGranted(source.isVoteGranted)
          .build()
    }
  }

  class FromRpcResponse : Converter<RpcRequestVoteResponse, RequestVoteResponse> {
    override fun convert(source: RpcRequestVoteResponse): RequestVoteResponse {
      return RequestVoteResponse(source.term, source.voteGranted)
    }
  }
}
