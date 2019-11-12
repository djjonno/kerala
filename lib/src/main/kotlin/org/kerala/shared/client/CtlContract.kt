package org.kerala.shared.client

import java.lang.StringBuilder

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
data class ReadTopics(val topics: List<TopicMeta>) : ClientResponse()

/**
 * ClusterDescription
 *
 * Describes state of cluster to a client.
 */
data class Node(val id: String, val host: String, val port: Int, val leader: Boolean = false)
data class ClusterDescription(val nodes: List<Node>) : ClientResponse() {
  override fun toString() = nodes.map { node ->
    "host \t${node.id}\t${if (node.leader) "leader" else ""}\n"
  }.reduce { acc, s -> acc + s }
}

/**
 * ClientSuccessResponse, ClientErrorResponse
 *
 * Accompany a code with a response message.
 */
data class ClientSuccessResponse(val message: String) : ClientResponse()
data class ClientErrorResponse(val message: String) : ClientResponse()
