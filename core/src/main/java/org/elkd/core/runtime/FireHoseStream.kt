package org.elkd.core.runtime

import org.apache.log4j.Logger
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.LogChangeListener
import org.elkd.core.runtime.client.ClientModule

/**
 * FireHoseStream routes all committed entries to their respective {@link Topic}s.
 */
class FireHoseStream(clientModule: ClientModule) {
  fun forward(entry: Entry) {
    logger.info("forwarding $entry")
  }

  /**
   * Listener component to bind the FireHoseStream to the Log.
   */
  inner class Listener : LogChangeListener<Entry> {
    override fun onCommit(index: Long, entry: Entry) {
      forward(entry)
    }

    override fun onAppend(index: Long, entry: Entry) {
      /* NoOp - we're not interested in append entries, only committed. */
    }
  }

  companion object {
    private var logger = Logger.getLogger(FireHoseStream::class.java)
  }
}
