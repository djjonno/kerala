@file:JvmMultifileClass
package org.ravine.core.server.cluster.exceptions

import org.ravine.core.RavineRuntimeException

open class ClusterException : RavineRuntimeException()

class NodeCommFailureException : ClusterException()

class NodeNotFoundException : ClusterException()
