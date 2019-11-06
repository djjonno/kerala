package org.kerala.core.log

interface LogEntry {
  /* Globally unique uuid for each entry. */
  val uuid: String
  val term: Int
}
