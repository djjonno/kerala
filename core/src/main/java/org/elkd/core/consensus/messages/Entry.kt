package org.elkd.core.consensus.messages

import org.elkd.core.log.LogEntry
import org.elkd.shared.annotations.Mockable
import java.util.*

@Mockable
class Entry private constructor(override val term: Int,
                                override val topic: String,
                                override val uuid: String,
                                val kvs: List<KV>) : LogEntry {
  class Builder internal constructor(private val term: Int, val topic: String) {
    val kvs = mutableListOf<KV>()
    fun addKV(kv: KV): Builder {
      kvs.add(kv)
      return this
    }
    fun addAllKV(kvs: List<KV>): Builder {
      this.kvs.addAll(kvs)
      return this
    }
    fun build() = Entry(term, topic, UUID.randomUUID().toString(), kvs)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other == null || javaClass != other.javaClass) {
      return false
    }
    val entry = other as Entry?
    return term == entry!!.term && topic == entry.topic
  }

  override fun hashCode(): Int {
    return Objects.hash(term, topic)
  }

  override fun toString(): String {
    return "Entry(uuid=$uuid, term=$term, topic=$topic, kvs=$kvs)"
  }

  companion object {
    @JvmStatic
    val NULL_ENTRY = builder(0, "default").build()

    @JvmStatic
    fun builder(term: Int, topic: String): Builder {
      return Builder(term, topic)
    }
  }
}
