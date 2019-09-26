package org.elkd.core.runtime.topic

import org.elkd.core.log.LogFactory

class TopicFactory(private val logFactory: LogFactory) {

  /**
   * Create a new topic and backing log.
   *
   * @param namespace name of topic to provision
   */
  fun create(namespace: String) = Topic(namespace, logFactory.createLog())
}
