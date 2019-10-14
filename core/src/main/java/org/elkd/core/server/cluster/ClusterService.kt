package org.elkd.core.server.cluster

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.elkd.core.consensus.RaftDelegate
import org.elkd.core.server.converters.AppendEntriesConverters
import org.elkd.core.server.converters.ConverterRegistry
import org.elkd.core.server.converters.RequestVoteConverters
import org.elkd.core.server.converters.StreamConverterDecorator

class ClusterService(
    private val raftDelegate: RaftDelegate,
    private val converterRegistry: ConverterRegistry
) : ElkdClusterServiceGrpc.ElkdClusterServiceImplBase() {

  init {
    LOGGER.info("Service ready to accept target connections")
  }

  override fun appendEntries(
      request: RpcAppendEntriesRequest,
      responseObserver: StreamObserver<RpcAppendEntriesResponse>
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

  override fun requestVote(
      request: RpcRequestVoteRequest,
      responseObserver: StreamObserver<RpcRequestVoteResponse>
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
