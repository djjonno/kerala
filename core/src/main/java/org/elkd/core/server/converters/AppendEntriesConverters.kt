package org.elkd.core.server.converters

import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.server.cluster.RpcAppendEntriesRequest
import org.elkd.core.server.cluster.RpcAppendEntriesResponse

class AppendEntriesConverters {

  class ToRpcRequest : Converter<AppendEntriesRequest, RpcAppendEntriesRequest> {
    private val converterRegistry: ConverterRegistry by ConverterRegistry.delegate

    override fun convert(source: AppendEntriesRequest): RpcAppendEntriesRequest {
      val converter = converterRegistry.getConverter<EntryConverters.ToRpc>()
      return RpcAppendEntriesRequest.newBuilder()
          .addAllEntries(source.entries.map(converter::convert))
          .setTerm(source.term)
          .setTopicId(source.topicId)
          .setLeaderId(source.leaderId)
          .setPrevLogTerm(source.prevLogTerm)
          .setPrevLogIndex(source.prevLogIndex)
          .setLeaderCommit(source.leaderCommit)
          .build()
    }
  }

  class FromRpcRequest : Converter<RpcAppendEntriesRequest, AppendEntriesRequest> {
    private val converterRegistry: ConverterRegistry by ConverterRegistry.delegate

    override fun convert(source: RpcAppendEntriesRequest): AppendEntriesRequest {
      val converter = converterRegistry.getConverter<EntryConverters.FromRpc>()
      return AppendEntriesRequest(
          source.term,
          source.topicId,
          source.prevLogTerm,
          source.prevLogIndex,
          source.leaderId,
          source.leaderCommit,
          source.entriesList.map(converter::convert))
    }
  }

  class ToRpcResponse : Converter<AppendEntriesResponse, RpcAppendEntriesResponse> {
    override fun convert(source: AppendEntriesResponse): RpcAppendEntriesResponse {
      return RpcAppendEntriesResponse.newBuilder()
          .setTerm(source.term)
          .setSuccess(source.isSuccessful)
          .build()
    }
  }

  class FromRpcResponse : Converter<RpcAppendEntriesResponse, AppendEntriesResponse> {
    override fun convert(source: RpcAppendEntriesResponse): AppendEntriesResponse {
        return AppendEntriesResponse(source.term, source.success)
    }
  }
}
