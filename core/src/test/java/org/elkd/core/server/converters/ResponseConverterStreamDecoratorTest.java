package org.elkd.core.server.converters;

import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ResponseConverterStreamDecoratorTest {
  @Mock Source mSource;
  @Mock Target mTarget;
  @Mock ConverterRegistry mConverterRegistry;
  @Mock StreamObserver<Target> mStreamObserver;

  private ResponseConverterStreamDecorator<Source, Target> mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new ResponseConverterStreamDecorator<>(mStreamObserver, mConverterRegistry);

    doReturn(mTarget)
        .when(mConverterRegistry)
        .convert(mSource);
  }

  @Test
  public void should_delegate_onNext_to_internal_stream() {
    // Given / When
    mUnitUnderTest.onNext(mSource);

    // Then
    verify(mStreamObserver).onNext(any());
  }

  @Test
  public void should_delegate_onError_to_internal_stream() {
    // Given
    final Throwable t = mock(Throwable.class);

    // When
    mUnitUnderTest.onError(t);

    // Then
    verify(mStreamObserver).onError(t);
  }

  @Test
  public void should_delegate_onCompleted_to_internal_stream() {
    // Given / When
    mUnitUnderTest.onCompleted();

    // Then
    verify(mStreamObserver).onCompleted();
  }

  @Test
  public void should_convert_message_onNext_pass_result_to_decoratedStream() {
    // Given / When
    mUnitUnderTest.onNext(mSource);

    // Then
    verify(mConverterRegistry).convert(mSource);
    verify(mStreamObserver).onNext(mTarget);
  }

  private static class Source { }
  private static class Target { }
}
