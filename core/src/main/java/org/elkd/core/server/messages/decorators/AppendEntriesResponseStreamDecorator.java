package org.elkd.core.server.messages.decorators;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.elkd.core.ElkdRuntimeException;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.server.RpcAppendEntriesResponse;
import org.elkd.core.server.messages.ConverterRegistry;

public class AppendEntriesResponseStreamDecorator implements StreamObserver<AppendEntriesResponse> {

  private final StreamObserver<RpcAppendEntriesResponse> mServerObserver;
  private final ConverterRegistry mConverterRegistry;

  public AppendEntriesResponseStreamDecorator(final StreamObserver<RpcAppendEntriesResponse> serverObserver,
                                              final ConverterRegistry converterRegistry) {
    mServerObserver = Preconditions.checkNotNull(serverObserver, "serverObserver");
    mConverterRegistry = Preconditions.checkNotNull(converterRegistry, "converterRegistry");
  }

  @Override
  public void onNext(final AppendEntriesResponse appendEntriesResponse) {
    try {
      final RpcAppendEntriesResponse response = mConverterRegistry.transform(RpcAppendEntriesResponse.class, appendEntriesResponse);
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
