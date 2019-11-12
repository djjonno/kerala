package org.kerala.shared.client

/**
 * These types will need to be moved to a shared library between server
 * and client.  This will establish a contract between the two, where
 * the contract can evolve correctly w/out risk of breaking clients.
 */

sealed class CtlResponse(val api: String = "v1")

/**
 * TopicMeta
 *
 * Describes a topic to a client.
 */
data class TopicMeta(
    val id: String,
    val namespace: String,
    val index: Long
) : CtlResponse() {
  private val fqn = "$namespace/$id"
  override fun toString() = "topic(${id})\tnamespace=${namespace}\tfqn=${fqn}\tindex=${index}"
}

/**
 * ReadTopics
 *
 * Describes a list of TopicMeta's to a client.
 */
data class ReadTopics(val topics: List<TopicMeta>) : CtlResponse() {
  override fun toString() = "${topics.size} topic(s)\n\n" + topics.map {
    it.toString()
  }.reduce { acc, s -> acc + s }
}

/**
 * ClusterDescription
 *
 * Describes state of cluster to a client.
 */
data class Node(val id: String, val host: String, val port: Int, val leader: Boolean = false)
data class ClusterDescription(val nodes: List<Node>) : CtlResponse() {

  /**
   * toString() defines the way this is printed in the console.
   */
  override fun toString() = "${nodes.size} nodes\n\n" + nodes.map { node ->
    "host\t${node.id}\t${if (node.leader) "leader" else ""}\n"
  }.reduce { acc, s -> acc + s }
}

/**
 * ClientSuccessResponse, ClientErrorResponse
 *
 * Accompany a code with a response message.
 */
data class CtlSuccessResponse(val message: String) : CtlResponse()
data class CtlErrorResponse(val message: String) : CtlResponse()
