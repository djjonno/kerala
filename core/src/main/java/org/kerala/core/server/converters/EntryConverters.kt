package org.kerala.core.server.converters

import org.kerala.core.consensus.messages.Entry
import org.kerala.core.server.cluster.KeralaEntry

class EntryConverters {
  class ToRpc : Converter<Entry, KeralaEntry> {
    private val converterRegistry: ConverterRegistry by ConverterRegistry.delegate

    override fun convert(source: Entry): KeralaEntry {
      val converter = converterRegistry.getConverter<KVConverters.ToRpc>()
      return KeralaEntry.newBuilder()
          .setUuid(source.uuid)
          .setTerm(source.term)
          .addAllKv(source.kvs.map(converter::convert))
          .build()
    }
  }

  class FromRpc : Converter<KeralaEntry, Entry> {
    private val converterRegistry: ConverterRegistry by ConverterRegistry.delegate

    override fun convert(source: KeralaEntry): Entry {
      val converter = converterRegistry.getConverter<KVConverters.FromRpc>()
      return Entry.builder(source.term, source.uuid)
          .addAllKV(source.kvList.map(converter::convert))
          .build()
    }
  }
}
