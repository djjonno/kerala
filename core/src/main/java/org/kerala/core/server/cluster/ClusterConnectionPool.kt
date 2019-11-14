package org.kerala.core.server.cluster

import com.google.common.util.concurrent.ListenableFuture
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.apache.log4j.Logger
import org.kerala.core.server.cluster.ClusterServiceGrpc.ClusterServiceFutureStub

class ClusterConnectionPool(val clusterSet: ClusterSet) {

  private val channelMap: Map<Node, Channel>

  init {
    LOGGER.info("init channel(s) to $clusterSet")
    channelMap = clusterSet.nodes.map { node ->
      node to Channel(ManagedChannelBuilder
          .forTarget(node.id)
          .usePlaintext() /* TODO: add cert auth */
          .build())
    }.toMap()
  }

  fun getChannel(node: Node): Channel? {
    return channelMap[node]
  }

  class Channel internal constructor(managedChannel: ManagedChannel,
                                     private val stub: ClusterServiceFutureStub = ClusterServiceGrpc.newFutureStub(managedChannel)) {

    fun appendEntries(request: RpcAppendEntriesRequest): ListenableFuture<RpcAppendEntriesResponse> {
      return stub.appendEntries(request)
    }

    fun requestVote(request: RpcRequestVoteRequest): ListenableFuture<RpcRequestVoteResponse> {
      return stub.requestVote(request)
    }
  }

  companion object {
    private val LOGGER = Logger.getLogger(ClusterConnectionPool::class.java)
  }
}
