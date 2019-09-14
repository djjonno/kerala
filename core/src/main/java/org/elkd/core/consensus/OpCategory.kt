package org.elkd.core.consensus

enum class OpCategory {
  /**
   * Denotes an operation that produces entries to the logger.
   *
   * @note execution allowed only on leader nodes..
   */
  PRODUCE,

  /**
   * Denotes an operation that requires leader execution.
   *
   * @note execution allowed only on leader nodes.
   */
  COMMAND,

  /**
   * Denotes an operation best suited for follower nodes.
   *
   * @note execution can occur on leader, preferably on a follower nodes.
   */
  CONSUME
}
