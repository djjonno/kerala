package org.elkd.core.runtime.topic

import org.elkd.core.log.LogFacade

/**
 * A topic represents a unique object which acts as the conduit between
 * a producer and a consumer.  The namespace property must be unique.
 */
data class Topic(val id: String,
                 val namespace: String,
                 val logFacade: LogFacade) {
  override fun toString() = "$namespace/$id"
}
