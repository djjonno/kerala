package org.kerala.core.server.converters

import org.kerala.core.consensus.messages.Entry
import org.kerala.core.server.cluster.RpcEntry

class EntryConverters {
  class ToRpc : Converter<Entry, RpcEntry> {
    private val converterRegistry: ConverterRegistry by ConverterRegistry.delegate

    override fun convert(source: Entry): RpcEntry {
      val converter = converterRegistry.getConverter<KVConverters.ToRpc>()
      return RpcEntry.newBuilder()
          .setUuid(source.uuid)
          .setTerm(source.term)
          .addAllKv(source.kvs.map(converter::convert))
          .build()
    }
  }

  class FromRpc : Converter<RpcEntry, Entry> {
    private val converterRegistry: ConverterRegistry by ConverterRegistry.delegate

    override fun convert(source: RpcEntry): Entry {
      val converter = converterRegistry.getConverter<KVConverters.FromRpc>()
      return Entry.builder(source.term, source.uuid)
          .addAllKV(source.kvList.map(converter::convert))
          .build()
    }
  }
}
