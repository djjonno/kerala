package org.elkd.core.runtime.topic

import org.elkd.core.log.LogFactory
import java.util.UUID

class TopicFactory(private val logFactory: LogFactory) {

  /**
   * Create a new topic and backing logFacade.
   *
   * @param namespace name of topic to provision
   */
  fun create(namespace: String, id: String = UUID.randomUUID().toString()) = Topic(id, namespace, logFactory.createLog())
}
