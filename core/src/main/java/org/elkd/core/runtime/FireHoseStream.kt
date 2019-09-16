package org.elkd.core.runtime

import org.apache.log4j.Logger
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.LogChangeListener
import org.elkd.core.runtime.client.ClientModule
import org.elkd.core.runtime.topic.Topic

/**
 * FireHoseStream routes all committed entries to their respective {@link Topic}s.
 */
class FireHoseStream(val clientModule: ClientModule) {
  fun forward(entry: Entry) {
    /* TODO: schedule on appropriate thread */
    when (entry.topic) {
      /* Routes to SystemConsumer */
      Topic.SYSTEM_TOPIC.namespace -> {
        clientModule.topicGateway.consumersFor(Topic.SYSTEM_TOPIC).forEach {
          it.consume(entry)
        }
      }
    }
  }

  /**
   * Listener component to bind the FireHoseStream to the Log.
   */
  inner class Listener : LogChangeListener<Entry> {
    override fun onCommit(index: Long, entry: Entry) {
      forward(entry)
    }
  }

  companion object {
    private var logger = Logger.getLogger(FireHoseStream::class.java)
  }
}
