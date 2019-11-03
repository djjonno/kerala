package org.ravine.core.consensus

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
    /* write persistent state to disk */
  }
}
