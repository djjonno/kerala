package org.elkd.core.consensus

import org.elkd.shared.annotations.Mockable

@Mockable
class RaftContext {
  /* persistent state */
  private var _currentTerm: Int = 0
  private var _votedFor: String? = null

  var currentTerm: Int
    get() = _currentTerm
    set(currentTerm) {
      _currentTerm = currentTerm
      commit()
    }

  var votedFor: String?
    get() = _votedFor
    set(votedFor) {
      _votedFor = votedFor
      commit()
    }

  private fun commit() {
    // TODO: Persist to disk https://elkd-issues.atlassian.net/browse/ELKD-9
    /* write persistent state to disk */
  }
}
