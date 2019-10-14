package org.elkd.core.consensus.messages

import java.util.UUID
import org.elkd.core.log.LogEntry

class Entry private constructor(
    override val term: Int,
    override val uuid: String,
    val kvs: List<KV>
) : LogEntry {
  override fun toString(): String {
    return "Entry(uuid=$uuid, term=$term, kvs=$kvs)"
  }

  companion object {
    @Deprecated("Logs will start from indexing from zero")
    @JvmStatic val NULL_ENTRY = builder(0, "null").build()

    @JvmStatic fun builder(term: Int, uuid: String = UUID.randomUUID().toString()) = Builder(term, uuid)
  }

  class Builder internal constructor(
      val term: Int,
      val uuid: String,
      private val kvs: MutableList<KV> = mutableListOf()
  ) {
    fun addKV(kv: KV): Builder {
      kvs.add(kv)
      return this
    }

    fun addAllKV(kvs: List<KV>): Builder {
      this.kvs.addAll(kvs)
      return this
    }

    fun build() = Entry(term, uuid, kvs)
  }
}
