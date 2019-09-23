package org.elkd.core.consensus

import org.elkd.core.consensus.messages.Request
import org.elkd.core.consensus.states.State

/**
 * Define a check to perform prior to delegation.
 */
interface TransitionContract {
  fun isTransitionRequired(request: Request): Boolean = true
  val transitionTo: State
  val transitionPreHook: (request: Request) -> Unit
  val transitionPostHook: (request: Request) -> Unit
}
