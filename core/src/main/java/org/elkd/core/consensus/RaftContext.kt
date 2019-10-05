package org.elkd.core.consensus

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.concurrent.ThreadSafe

@ThreadSafe class RaftContext {
  private var _currentTerm: AtomicInteger = AtomicInteger(0)
  private var _votedFor: AtomicReference<String> = AtomicReference("")

  var currentTerm: Int
    get() = _currentTerm.get()
    set(currentTerm) {
      _currentTerm.set(currentTerm)
      commit()
    }

  var votedFor: String?
    get() = _votedFor.get()
    set(votedFor) {
      _votedFor.set(votedFor)
      commit()
    }

  private fun commit() {
    // TODO: Persist to disk https://elkd-issues.atlassian.net/browse/ELKD-9
    /* write persistent state to disk */
  }
}
