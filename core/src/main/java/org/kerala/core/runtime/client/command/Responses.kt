package org.kerala.core.runtime.client.command

import org.kerala.core.runtime.client.broker.ClusterSetInfo
import org.kerala.core.runtime.topic.Topic

/**
 * These types will need to be moved to a shared library between server
 * and client.  This will establish a contract between the two, where
 * the contract can evolve correctly w/out risk of breaking clients.
 */

sealed class ClientResponse(val api: String = "v1")

/**
 * TopicMeta
 *
 * Describes a topic to a client.
 */
data class TopicMeta(
    val namespace: String,
    val index: Long
) : ClientResponse()

/**
 * ReadTopics
 *
 * Describes a list of TopicMeta's to a client.
 */
class ReadTopics(topics: List<Topic>) : ClientResponse() {
  val topics = topics.map { TopicMeta(it.namespace, it.logFacade.log.commitIndex) }
}

/**
 * ClusterInfo
 *
 * Describes state of cluster to a client.
 */
class Node(val host: String, val port: Int, val leader: Boolean = false) : ClientResponse()
class ClusterInfo(clusterSetInfo: ClusterSetInfo) : ClientResponse() {
  val nodes = clusterSetInfo.describe().map { Node(it.host, it.port, it == clusterSetInfo.leader) }
}

/**
 * ClientSuccessResponse, ClientErrorResponse
 *
 * Accompany a code with a response message.
 */
class ClientSuccessResponse(val message: String) : ClientResponse()
class ClientErrorResponse(val message: String) : ClientResponse()
