package org.elkd.core.log

import java.util.*

interface LogEntry {
  val term: Int
  val topic: String

  /**
   * Globally unique id for each entry.
   */
  val id: String
    get() = UUID.randomUUID().toString()
}
