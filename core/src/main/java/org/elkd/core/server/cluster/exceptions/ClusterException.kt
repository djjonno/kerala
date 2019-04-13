@file:JvmMultifileClass
package org.elkd.core.server.cluster.exceptions

import org.elkd.core.ElkdRuntimeException

open class ClusterException : ElkdRuntimeException()

class NodeCommFailureException : ClusterException()

class NodeNotFoundException : ClusterException()
