package org.elkd.core.server.converters;

import com.google.common.base.Preconditions;
import io.grpc.stub.StreamObserver;
import org.elkd.core.ElkdRuntimeException;

public class ResponseConverterStreamDecorator<Source, Target> implements StreamObserver<Source> {

  private final StreamObserver<Target> mTargetObserver;
  private final ConverterRegistry mConverterRegistry;

  public ResponseConverterStreamDecorator(final StreamObserver<Target> targetObserver,
                                          final ConverterRegistry converterRegistry) {
    mTargetObserver = Preconditions.checkNotNull(targetObserver, "targetObserver");
    mConverterRegistry = Preconditions.checkNotNull(converterRegistry, "converterRegistry");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onNext(final Source source) {
    try {
      final Target response = mConverterRegistry.convert(source);
      mTargetObserver.onNext(response);
    } catch (final ElkdRuntimeException e) {
      onError(e);
    }
  }

  @Override
  public void onError(final Throwable t) {
    mTargetObserver.onError(t);
  }

  @Override
  public void onCompleted() {
    mTargetObserver.onCompleted();
  }
}
