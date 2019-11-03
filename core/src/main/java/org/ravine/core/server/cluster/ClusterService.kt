package org.ravine.core.server.cluster

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger
import org.ravine.core.consensus.RaftDelegate
import org.ravine.core.server.converters.AppendEntriesConverters
import org.ravine.core.server.converters.ConverterRegistry
import org.ravine.core.server.converters.RequestVoteConverters
import org.ravine.core.server.converters.StreamConverterDecorator

class ClusterService(
    private val raftDelegate: RaftDelegate,
    private val converterRegistry: ConverterRegistry
) : ClusterServiceGrpc.ClusterServiceImplBase() {

  init {
    LOGGER.info("cluster service ready")
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
