package org.elkd.core.consensus

import org.apache.log4j.Logger
import org.elkd.shared.annotations.Mockable

@Mockable
class RaftContext {
  /* persistent state */
  private var _currentTerm: Int = 0
  private var _votedFor: String? = null

  init {
    _currentTerm = 0
    _votedFor = null
  }

  var currentTerm: Int
    get() = _currentTerm
    set(currentTerm) {
      LOG.info("setting currentTerm to $currentTerm")
      _currentTerm = currentTerm
      commit()
    }

  var votedFor: String?
    get() = _votedFor
    set(votedFor) {
      LOG.info("setting votedFor to $votedFor")
      _votedFor = votedFor
      commit()
    }

  private fun commit() {
    // TODO: Persist to disk https://elkd-issues.atlassian.net/browse/ELKD-9
    /* write persistent state to disk */
  }

  companion object {
    private val LOG = Logger.getLogger(RaftContext::class.java.name)
  }
}
