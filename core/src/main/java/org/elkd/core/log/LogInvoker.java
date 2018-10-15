package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.elkd.core.log.listener.LogChangeListener;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LogInvoker<T> implements Log<T> {
  private final Set<LogChangeListener<T>> mListeners = new HashSet<>();
  private final Log<T> mLog;

  public LogInvoker(@Nonnull final Log<T> log) {
    mLog = Preconditions.checkNotNull(log, "log");
  }

  @Override
  public long append(final T entry) {
    final long index = mLog.append(entry);
    onAppend(ImmutableList.of(entry));
    return index;
  }

  @Override
  public long append(final List<T> entries) {
    final long index = mLog.append(entries);
    onAppend(entries);
    return index;
  }

  @Override
  public T read(final long index) {
    return mLog.read(index);
  }

  @Override
  public List<T> read(final long from, final long to) {
    return mLog.read(from, to);
  }

  @Override
  public CommitResult<T> commit(final long index) {
    final CommitResult<T> result = mLog.commit(index);
    onCommit(result.getCommitted());
    return result;
  }

  @Override
  public void revert(final long index) {
    mLog.revert(index);
  }

  public void registerListener(@Nonnull final LogChangeListener<T> listener) {
    Preconditions.checkNotNull(listener, "listener");
    mListeners.add(listener);
  }

  public void deregisterListener(@Nonnull final LogChangeListener<T> listener) {
    Preconditions.checkNotNull(listener, "listener");
    mListeners.remove(listener);
  }

  private void onCommit(final List<T> entries) {
    for (final LogChangeListener<T> listener : mListeners) {
      listener.onCommit(entries);
    }
  }

  private void onAppend(final List<T> entries) {
    for (final LogChangeListener<T> listener : mListeners) {
      listener.onAppend(entries);
    }
  }
}
