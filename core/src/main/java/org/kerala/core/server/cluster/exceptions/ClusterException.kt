@file:JvmMultifileClass
package org.kerala.core.server.cluster.exceptions

import org.kerala.core.KeralaRuntimeException

open class ClusterException : KeralaRuntimeException()

class NodeCommFailureException : ClusterException()

class NodeNotFoundException : ClusterException()
