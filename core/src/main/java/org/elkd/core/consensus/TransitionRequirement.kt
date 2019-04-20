package org.elkd.core.consensus

import org.elkd.core.consensus.messages.Request

/**
 * Define a check to perform prior to delegation.
 */
interface TransitionRequirement {
  fun isTransitionRequired(request: Request): Boolean = true
  val transitionTo: State
  val onTransitionPreHook: (request: Request) -> Unit
  val onTransitionPostHook: (request: Request) -> Unit
}
