package org.elkd.core.consensus

enum class OpCategory {
  /**
   * Denotes a write operation that changes state.
   *
   * @note execution allowed only on leader nodes..
   */
  WRITE,

  /**
   * Denotes a read operation that can be executed on any node.
   */
  READ
}
