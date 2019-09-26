package org.elkd.core.server.converters

import org.elkd.core.consensus.messages.KV
import org.elkd.core.server.client.RpcKV

class KVConverters {
  class ToRpc : Converter<KV, RpcKV> {
    override fun convert(source: KV): RpcKV =
        RpcKV.newBuilder()
            .setKey(source.key)
            .setValue(source.`val`).build()
  }

  class FromRpc : Converter<RpcKV, KV> {
    override fun convert(source: RpcKV): KV =
        KV(source.key, source.value)
  }
}
