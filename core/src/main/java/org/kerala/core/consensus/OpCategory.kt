package org.kerala.core.consensus

enum class OpCategory {
  /**
   * Denotes a write operation that changes state.
   *
   * @note execution allowed only on leader nodes..
   */
  WRITE,

  /**
   * Denotes a read operation.
   *
   * @note execution allowed on any node.
   */
  READ
}
