package org.elkd.core.runtime.topic

import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.LogFacade

class TopicFactory(private val logFacade: LogFacade<Entry>) {

  /**
   * Create a new topic and backing log.
   *
   * @param Topic topic to provision
   */
  fun create(namespace: String) = Topic(namespace, logFacade.createLog())
}
