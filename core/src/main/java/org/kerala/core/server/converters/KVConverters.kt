package org.kerala.core.server.converters

import com.google.protobuf.ByteString
import org.kerala.core.consensus.messages.KV
import org.kerala.core.server.client.KeralaKV

class KVConverters {
  class ToRpc : Converter<KV, KeralaKV> {
    override fun convert(source: KV): KeralaKV =
        KeralaKV.newBuilder()
            .setKey(ByteString.copyFrom(source.key))
            .setValue(ByteString.copyFrom(source.value))
            .setTimestamp(source.timestamp)
            .build()
  }

  class FromRpc : Converter<KeralaKV, KV> {
    override fun convert(source: KeralaKV): KV =
        KV(source.key.toByteArray(), source.value.toByteArray(), source.timestamp)
  }
}
