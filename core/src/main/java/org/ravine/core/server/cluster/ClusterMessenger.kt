package org.ravine.core.server.cluster

import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import org.apache.log4j.Logger
import org.ravine.core.consensus.messages.AppendEntriesRequest
import org.ravine.core.consensus.messages.AppendEntriesResponse
import org.ravine.core.consensus.messages.RequestVoteRequest
import org.ravine.core.consensus.messages.RequestVoteResponse
import org.ravine.core.server.cluster.exceptions.NodeNotFoundException
import org.ravine.core.server.converters.AppendEntriesConverters
import org.ravine.core.server.converters.ConverterRegistry
import org.ravine.core.server.converters.RequestVoteConverters
import org.ravine.shared.annotations.Mockable

@Mockable
class ClusterMessenger(
    private val clusterConnectionPool: org.ravine.core.server.cluster.ClusterConnectionPool,
    private val converterRegistry: ConverterRegistry = ConverterRegistry.instance
) : CoroutineScope {
  val clusterSet: ClusterSet
    get() = clusterConnectionPool.clusterSet

  override val coroutineContext: CoroutineContext
    get() = Job() + Dispatchers.IO

  suspend fun dispatchAppendEntries(
      node: Node,
      message: AppendEntriesRequest,
      onSuccess: (response: AppendEntriesResponse) -> Unit = {},
      onFailure: (e: Exception) -> Unit = {}
  ) {
    val channel = getChannel(node)

    try {
      coroutineScope {
        val listenableFuture = channel.appendEntries(converterRegistry.getConverter<AppendEntriesConverters.ToRpcRequest>().convert(message))
        val response = listenableFuture.get(1, TimeUnit.SECONDS)
        onSuccess(converterRegistry.getConverter<AppendEntriesConverters.FromRpcResponse>().convert(response))
      }
    } catch (e: Exception) {
      /* cluster messaging is best effort */
      LOGGER.warn("node unreachable: $node")
      onFailure(e)
    }
  }

  suspend fun dispatchRequestVote(
      node: Node,
      message: RequestVoteRequest,
      onSuccess: (response: RequestVoteResponse) -> Unit = {},
      onFailure: (e: Exception) -> Unit = {}
  ) {
    val channel = getChannel(node)

    try {
      coroutineScope {
        val listenableFuture = channel.requestVote(converterRegistry.getConverter<RequestVoteConverters.ToRpcRequest>().convert(message))
        val response = listenableFuture.get(1, TimeUnit.SECONDS)
        onSuccess(converterRegistry.getConverter<RequestVoteConverters.FromRpcResponse>().convert(response))
      }
    } catch (e: Exception) {
      /* cluster messaging is best effort */
      LOGGER.warn("node unreachable: $node")
      onFailure(e)
    }
  }

  private fun getChannel(node: Node): org.ravine.core.server.cluster.ClusterConnectionPool.Channel {
    return (clusterConnectionPool.getChannel(node) ?: throw NodeNotFoundException())
  }

  private companion object {
    val LOGGER: Logger = Logger.getLogger(ClusterMessenger::class.java)
  }
}
