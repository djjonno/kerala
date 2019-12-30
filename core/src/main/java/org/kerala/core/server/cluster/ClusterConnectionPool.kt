package org.kerala.core.server.cluster

import com.google.common.util.concurrent.ListenableFuture
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.kerala.core.server.cluster.KeralaClusterServiceGrpc.KeralaClusterServiceFutureStub
import org.kerala.shared.logger

class ClusterConnectionPool(val clusterSet: ClusterSet) {

  private val channelMap: Map<Node, Channel>

  init {
    logger("init channel(s) to $clusterSet")
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
                                     private val stub: KeralaClusterServiceFutureStub = KeralaClusterServiceGrpc.newFutureStub(managedChannel)) {

    fun appendEntries(request: KeralaAppendEntriesRequest): ListenableFuture<KeralaAppendEntriesResponse> {
      return stub.keralaAppendEntries(request)
    }

    fun requestVote(request: KeralaRequestVoteRequest): ListenableFuture<KeralaRequestVoteResponse> {
      return stub.keralaRequestVote(request)
    }
  }
}
