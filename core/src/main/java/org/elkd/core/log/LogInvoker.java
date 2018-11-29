package org.elkd.core.log;

import com.google.common.base.Preconditions;

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
    onAppend(entry);
    return index;
  }

  @Override
  public long append(final long index, final T entry) {
    final long append = mLog.append(index, entry);
    onAppend(entry);
    return append;
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

  @Override
  public long getCommitIndex() {
    return mLog.getCommitIndex();
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
      entries.forEach(listener::onCommit);
    }
  }

  private void onAppend(final T entry) {
    for (final LogChangeListener<T> listener : mListeners) {
      listener.onAppend(entry);
    }
  }
}
