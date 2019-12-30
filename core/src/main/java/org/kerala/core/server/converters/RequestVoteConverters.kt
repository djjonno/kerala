package org.kerala.core.server.converters

import org.kerala.core.consensus.messages.RequestVoteRequest
import org.kerala.core.consensus.messages.RequestVoteResponse
import org.kerala.core.consensus.messages.TopicTail
import org.kerala.core.server.cluster.KeralaLogTail
import org.kerala.core.server.cluster.KeralaRequestVoteRequest
import org.kerala.core.server.cluster.KeralaRequestVoteResponse

class RequestVoteConverters {
  class ToRpcRequest : Converter<RequestVoteRequest, KeralaRequestVoteRequest> {
    override fun convert(source: RequestVoteRequest): KeralaRequestVoteRequest {
      return KeralaRequestVoteRequest.newBuilder()
          .setTerm(source.term)
          .setCandidateId(source.candidateId)
          .addAllLogTails(source.topicTails.map {
            KeralaLogTail.newBuilder()
                .setTopicId(it.topicId)
                .setLastLogIndex(it.lastLogIndex)
                .setLastLogTerm(it.lastLogTerm)
                .build()
          })
          .build()
    }
  }

  class FromRpcRequest : Converter<KeralaRequestVoteRequest, RequestVoteRequest> {
    override fun convert(source: KeralaRequestVoteRequest): RequestVoteRequest {
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

  class ToRpcResponse : Converter<RequestVoteResponse, KeralaRequestVoteResponse> {
    override fun convert(source: RequestVoteResponse): KeralaRequestVoteResponse {
      return KeralaRequestVoteResponse.newBuilder()
          .setTerm(source.term)
          .setVoteGranted(source.isVoteGranted)
          .build()
    }
  }

  class FromRpcResponse : Converter<KeralaRequestVoteResponse, RequestVoteResponse> {
    override fun convert(source: KeralaRequestVoteResponse): RequestVoteResponse {
      return RequestVoteResponse(source.term, source.voteGranted)
    }
  }
}
