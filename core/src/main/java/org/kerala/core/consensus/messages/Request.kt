package org.kerala.core.consensus.messages

/**
 * Common interface for raft requests.
 */
interface Request {
  val term: Int
}
