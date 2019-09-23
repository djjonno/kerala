package org.elkd.core.runtime.topic

import org.elkd.core.log.LogComponents
import org.elkd.core.log.ds.Log
import org.elkd.core.log.LogEntry

/**
 * A topic represents a unique object which acts as the conduit between
 * a producer and a consumer.  The namespace property must be unique.
 */
data class Topic(val namespace: String, val log: LogComponents)
