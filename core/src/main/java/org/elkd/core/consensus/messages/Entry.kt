package org.elkd.core.consensus.messages

import org.elkd.core.log.LogEntry
import org.elkd.shared.annotations.Mockable
import java.util.*

@Mockable
class Entry private constructor(override val term: Int,
                                override val topic: String) : LogEntry {

  class Builder internal constructor(private val term: Int, val topic: String) {
    fun build() = Entry(term, topic)
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
    return "Entry(term=$term, topic=$topic)"
  }

  companion object {
    val NULL_ENTRY = Entry.builder(0, "default").build()

    @JvmStatic
    fun builder(term: Int, topic: String): Builder {
      return Builder(term, topic)
    }
  }
}
