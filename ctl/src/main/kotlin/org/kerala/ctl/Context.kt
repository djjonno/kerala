package org.kerala.ctl

import io.grpc.ManagedChannel

object Context {
  /**
   * Channel is configured by `Tool`, making it available to subcommands downstream.
   * Subcommands can guarantee that this value is set/non-nullable
   */
  var channel: ManagedChannel? = null
}
