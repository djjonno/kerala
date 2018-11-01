package org.elkd.core.server.payload;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.elkd.core.ElkdRuntimeException;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.server.RpcAppendEntriesResponse;

public class AppendEntriesResponseStreamDecorator implements StreamObserver<AppendEntriesResponse> {

  private final StreamObserver<org.elkd.core.server.RpcAppendEntriesResponse> mServerObserver;
  private final PayloadAdapterRegistry mPayloadAdapterRegistry;

  public AppendEntriesResponseStreamDecorator(
      final StreamObserver<RpcAppendEntriesResponse> serverObserver,
      final PayloadAdapterRegistry payloadAdapterRegistry
  ) {
    mServerObserver = Preconditions.checkNotNull(serverObserver, "serverObserver");
    mPayloadAdapterRegistry = Preconditions.checkNotNull(payloadAdapterRegistry, "payloadAdapterRegistry");
  }

  @Override
  public void onNext(final AppendEntriesResponse appendEntriesResponse) {
    try {
      final RpcAppendEntriesResponse response = mPayloadAdapterRegistry.transform(RpcAppendEntriesResponse.class, appendEntriesResponse);
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
