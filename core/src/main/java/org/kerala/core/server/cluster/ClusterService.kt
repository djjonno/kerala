package org.kerala.core.server.cluster

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.kerala.core.consensus.RaftDelegate
import org.kerala.core.server.converters.AppendEntriesConverters
import org.kerala.core.server.converters.ConverterRegistry
import org.kerala.core.server.converters.RequestVoteConverters
import org.kerala.core.server.converters.StreamConverterDecorator

class ClusterService(
    private val raftDelegate: RaftDelegate,
    private val converterRegistry: ConverterRegistry
) : KeralaClusterServiceGrpc.KeralaClusterServiceImplBase() {

  init {
    LOGGER.info("cluster service ready")
  }

  override fun keralaAppendEntries(
      request: KeralaAppendEntriesRequest,
      responseObserver: StreamObserver<KeralaAppendEntriesResponse>
  ) {
    try {
      raftDelegate.delegateAppendEntries(
          converterRegistry.getConverter<AppendEntriesConverters.FromRpcRequest>().convert(request),
          StreamConverterDecorator(responseObserver, AppendEntriesConverters.ToRpcResponse())
      )
    } catch (e: Exception) {
      LOGGER.error(e)
      responseObserver.onError(e)
      responseObserver.onCompleted()
    }
  }

  override fun keralaRequestVote(
      request: KeralaRequestVoteRequest,
      responseObserver: StreamObserver<KeralaRequestVoteResponse>
  ) {
    try {
      raftDelegate.delegateRequestVote(
          converterRegistry.getConverter<RequestVoteConverters.FromRpcRequest>().convert(request),
          StreamConverterDecorator(responseObserver, RequestVoteConverters.ToRpcResponse())
      )
    } catch (e: Exception) {
      LOGGER.error(e)
      responseObserver.onError(e)
      responseObserver.onCompleted()
    }
  }

  companion object {
    private val LOGGER = Logger.getLogger(ClusterService::class.java)
  }
}
