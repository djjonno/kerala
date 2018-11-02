package org.elkd.core.server.messages.decorators;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.elkd.core.ElkdRuntimeException;
import org.elkd.core.consensus.messages.RequestVotesResponse;
import org.elkd.core.server.RpcRequestVotesResponse;
import org.elkd.core.server.messages.ConverterRegistry;

public class RequestVotesResponseStreamDecorator implements StreamObserver<RequestVotesResponse> {

  private final StreamObserver<RpcRequestVotesResponse> mServerObserver;
  private final ConverterRegistry mConverterRegistry;

  public RequestVotesResponseStreamDecorator(final StreamObserver<RpcRequestVotesResponse> serverObserver,
                                             final ConverterRegistry converterRegistry) {
    mServerObserver = Preconditions.checkNotNull(serverObserver, "serverObserver");
    mConverterRegistry = Preconditions.checkNotNull(converterRegistry, "converterRegistry");
  }

  @Override
  public void onNext(final RequestVotesResponse requestVotesResponse) {
    try {
      final RpcRequestVotesResponse response = mConverterRegistry.transform(RpcRequestVotesResponse.class, requestVotesResponse);
      mServerObserver.onNext(response);
    } catch (final ElkdRuntimeException e) {
      onError(e);
    }
  }

  @Override
  public void onError(final Throwable t) {
    mServerObserver.onError(t);
  }

  @Override
  public void onCompleted() {
    mServerObserver.onCompleted();
  }
}
