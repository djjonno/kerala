package org.elkd.core.server.cluster

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.apache.log4j.Logger
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.AppendEntriesResponse
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.consensus.messages.RequestVoteResponse
import org.elkd.core.server.cluster.exceptions.NodeNotFoundException
import org.elkd.core.server.converters.AppendEntriesConverters
import org.elkd.core.server.converters.Converter
import org.elkd.core.server.converters.ConverterRegistry
import org.elkd.core.server.converters.RequestVoteConverters
import org.elkd.shared.annotations.Mockable
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

@Mockable
class ClusterMessenger (private val clusterConnectionPool: ClusterConnectionPool,
                        private val converterRegistry: ConverterRegistry = ConverterRegistry.instance): CoroutineScope {
  val clusterSet: ClusterSet
    get() = clusterConnectionPool.clusterSet

  override val coroutineContext: CoroutineContext
    get() = Job() + Dispatchers.IO

  suspend fun dispatchAppendEntries(node: Node, message: AppendEntriesRequest): AppendEntriesResponse {
    val channel = getChannel(node)

    val listenableFuture = channel.appendEntries(converterRegistry.getConverter<AppendEntriesConverters.ToRpcRequest>().convert(message))
    return coroutineScope {
      val response = listenableFuture.get(1, TimeUnit.SECONDS)
      converterRegistry.getConverter<AppendEntriesConverters.FromRpcResponse>().convert(response)
    }
  }

  suspend fun dispatchRequestVote(node: Node, message: RequestVoteRequest): RequestVoteResponse {
    val channel = getChannel(node)

    val listenableFuture = channel.requestVote(converterRegistry.getConverter<RequestVoteConverters.ToRpcRequest>().convert(message))
    return coroutineScope {
      val response = listenableFuture.get(1, TimeUnit.SECONDS)
      converterRegistry.getConverter<RequestVoteConverters.FromRpcResponse>().convert(response)
    }
  }

  private fun getChannel(node: Node): ClusterConnectionPool.Channel {
    return (clusterConnectionPool.getChannel(node) ?: throw NodeNotFoundException())
  }
}
