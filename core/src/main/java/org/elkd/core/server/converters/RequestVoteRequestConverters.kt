package org.elkd.core.server.converters

import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.elkd.core.server.cluster.RpcRequestVoteRequest
import org.elkd.core.server.cluster.RpcRequestVoteResponse

class RequestVoteConverters {
  class ToRpcRequest : Converter<RequestVoteRequest, RpcRequestVoteRequest> {
    override fun convert(source: RequestVoteRequest): RpcRequestVoteRequest {
      return RpcRequestVoteRequest.newBuilder()
          .setTerm(source.term)
          .setCandidateId(source.candidateId)
          .setLastLogIndex(source.lastLogIndex)
          .setLastLogTerm(source.lastLogTerm)
          .build()
    }
  }

  class FromRpcRequest : Converter<RpcRequestVoteRequest, RequestVoteRequest> {
    override fun convert(source: RpcRequestVoteRequest): RequestVoteRequest {
      return RequestVoteRequest.builder(
          source.term,
          source.candidateId,
          source.lastLogIndex,
          source.lastLogTerm
      ).build()
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
      return RequestVoteResponse.builder(source.term, source.voteGranted).build()
    }
  }
}
