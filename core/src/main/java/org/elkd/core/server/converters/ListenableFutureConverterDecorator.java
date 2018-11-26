package org.elkd.core.server.converters;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ListenableFutureConverterDecorator<Source, Target> implements ListenableFuture<Target> {
  final ListenableFuture<Source> mListenableFuture;
  final ConverterRegistry mConverterRegistry;

  public ListenableFutureConverterDecorator(final ListenableFuture<Source> listenableFuture,
                                            final ConverterRegistry converterRegistry) {
    mListenableFuture = Preconditions.checkNotNull(listenableFuture, "listenableFuture");
    mConverterRegistry = Preconditions.checkNotNull(converterRegistry, "converterRegistry");
  }

  @Override
  public void addListener(final Runnable listener, final Executor executor) {
    mListenableFuture.addListener(listener, executor);
  }

  @Override
  public boolean cancel(final boolean mayInterruptIfRunning) {
    return mListenableFuture.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return mListenableFuture.isCancelled();
  }

  @Override
  public boolean isDone() {
    return mListenableFuture.isDone();
  }

  @Override
  public Target get() throws InterruptedException, ExecutionException {
    return mConverterRegistry.convert(mListenableFuture.get());
  }

  @Override
  public Target get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return mConverterRegistry.convert(mListenableFuture.get(timeout, unit));
  }
}
