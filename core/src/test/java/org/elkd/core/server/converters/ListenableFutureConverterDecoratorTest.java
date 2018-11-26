package org.elkd.core.server.converters;

import com.google.common.util.concurrent.ListenableFuture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ListenableFutureConverterDecoratorTest {

  private static final boolean CANCELLED = true;
  private static final boolean IS_CANCELLED = true;
  private static final boolean IS_DONE = true;

  @Mock ConverterRegistry mConverterRegistry;
  @Mock ListenableFuture<Source> mDecoratedFuture;

  @Mock Source mSource;
  @Mock Target mTarget;

  private ListenableFutureConverterDecorator<Source, Target> mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    doReturn(mTarget)
        .when(mConverterRegistry)
        .convert(mSource);

    /* mock delegate */

    doReturn(mSource)
        .when(mDecoratedFuture)
        .get();

    doReturn(mSource)
        .when(mDecoratedFuture)
        .get(anyLong(), any(TimeUnit.class));

    doReturn(IS_CANCELLED)
        .when(mDecoratedFuture)
        .isCancelled();

    doReturn(CANCELLED)
        .when(mDecoratedFuture)
        .cancel(true);

    doReturn(IS_DONE)
        .when(mDecoratedFuture)
        .isDone();

    mUnitUnderTest = new ListenableFutureConverterDecorator<>(mDecoratedFuture, mConverterRegistry);
  }

  @Test
  public void should_delegate_addListener_to_decorated_future() {
    // Given
    final Runnable runnable = mock(Runnable.class);
    final ExecutorService executor = mock(ExecutorService.class);

    // When
    mUnitUnderTest.addListener(runnable, executor);

    // Then
    verify(mDecoratedFuture).addListener(runnable, executor);
  }

  @Test
  public void should_delegate_cancel_to_decorated_future() {
    // Given / When
    final boolean cancel = true;
    final boolean cancelled = mUnitUnderTest.cancel(cancel);

    // Then
    assertTrue(cancelled);
    verify(mDecoratedFuture).cancel(cancel);
  }

  @Test
  public void should_delegate_isCancelled_to_decorated_future() {
    // Given / When
    final boolean cancelled = mUnitUnderTest.isCancelled();

    // Then
    verify(mDecoratedFuture).isCancelled();
    assertTrue(IS_CANCELLED && cancelled);
  }

  @Test
  public void should_delegate_isDone_to_decorated_future() {
    // Given / When
    final boolean done = mUnitUnderTest.isDone();

    // Then
    verify(mDecoratedFuture).isDone();
    assertEquals(IS_DONE, done);
  }

  @Test
  public void should_delegate_get_to_decorated_future() throws ExecutionException, InterruptedException {
    // Given / When
    final Target target = mUnitUnderTest.get();

    // Then
    verify(mDecoratedFuture).get();
    verify(mConverterRegistry).convert(mSource);
    assertSame(mTarget, target);
  }

  @Test
  public void should_delegate_getWithTimeout_to_decorated_future() throws InterruptedException, ExecutionException, TimeoutException {
    // Given / When
    final int timeout = 1;
    final TimeUnit unit = TimeUnit.SECONDS;
    final Target target = mUnitUnderTest.get(timeout, unit);

    // Then
    verify(mDecoratedFuture).get(timeout, unit);
    verify(mConverterRegistry).convert(mSource);
    assertSame(mTarget, target);
  }

  @Test(expected = InterruptedException.class)
  public void should_throw_exception_when_get_fails() throws ExecutionException, InterruptedException {
    // Given
    doThrow(new InterruptedException())
        .when(mDecoratedFuture)
        .get();

    // When
    mUnitUnderTest.get();

    // Then - exception thrown
  }

  private static class Source { }
  private static class Target { }
}
