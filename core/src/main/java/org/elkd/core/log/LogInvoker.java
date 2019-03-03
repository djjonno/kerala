package org.elkd.core.log;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LogInvoker<E> implements Log<E> {
  private final Set<LogChangeListener<E>> mListeners = new HashSet<>();
  private final Log<E> mLog;

  public LogInvoker(@Nonnull final Log<E> log) {
    mLog = Preconditions.checkNotNull(log, "log");
  }

  @Override
  public long append(final E entry) {
    final long index = mLog.append(entry);
    onAppend(entry);
    return index;
  }

  @Override
  public long append(final long index, final E entry) {
    final long append = mLog.append(index, entry);
    onAppend(entry);
    return append;
  }

  @Override
  public E read(final long index) {
    return mLog.read(index);
  }

  @Override
  public List<E> read(final long from, final long to) {
    return mLog.read(from, to);
  }

  @Override
  public CommitResult<E> commit(final long index) {
    final CommitResult<E> result = mLog.commit(index);
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

  @Override
  public long getLastIndex() {
    return mLog.getLastIndex();
  }

  public void registerListener(@Nonnull final LogChangeListener<E> listener) {
    Preconditions.checkNotNull(listener, "listener");
    mListeners.add(listener);
  }

  public void deregisterListener(@Nonnull final LogChangeListener<E> listener) {
    Preconditions.checkNotNull(listener, "listener");
    mListeners.remove(listener);
  }

  private void onCommit(final List<E> entries) {
    for (final LogChangeListener<E> listener : mListeners) {
      entries.forEach(listener::onCommit);
    }
  }

  private void onAppend(final E entry) {
    for (final LogChangeListener<E> listener : mListeners) {
      listener.onAppend(entry);
    }
  }
}
