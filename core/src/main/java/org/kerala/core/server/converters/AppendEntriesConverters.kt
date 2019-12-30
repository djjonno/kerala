package org.kerala.core.server.converters

import org.kerala.core.consensus.messages.AppendEntriesRequest
import org.kerala.core.consensus.messages.AppendEntriesResponse
import org.kerala.core.server.cluster.KeralaAppendEntriesRequest
import org.kerala.core.server.cluster.KeralaAppendEntriesResponse

class AppendEntriesConverters {

  class ToRpcRequest : Converter<AppendEntriesRequest, KeralaAppendEntriesRequest> {
    private val converterRegistry: ConverterRegistry by ConverterRegistry.delegate

    override fun convert(source: AppendEntriesRequest): KeralaAppendEntriesRequest {
      val converter = converterRegistry.getConverter<EntryConverters.ToRpc>()
      return KeralaAppendEntriesRequest.newBuilder()
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

  class FromRpcRequest : Converter<KeralaAppendEntriesRequest, AppendEntriesRequest> {
    private val converterRegistry: ConverterRegistry by ConverterRegistry.delegate

    override fun convert(source: KeralaAppendEntriesRequest): AppendEntriesRequest {
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

  class ToRpcResponse : Converter<AppendEntriesResponse, KeralaAppendEntriesResponse> {
    override fun convert(source: AppendEntriesResponse): KeralaAppendEntriesResponse {
      return KeralaAppendEntriesResponse.newBuilder()
          .setTerm(source.term)
          .setSuccess(source.isSuccessful)
          .setPrevLogIndex(source.prevLogIndex)
          .build()
    }
  }

  class FromRpcResponse : Converter<KeralaAppendEntriesResponse, AppendEntriesResponse> {
    override fun convert(source: KeralaAppendEntriesResponse): AppendEntriesResponse {
        return AppendEntriesResponse(source.term, source.success, source.prevLogIndex)
    }
  }
}
