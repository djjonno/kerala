package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.elkd.core.raft.messages.Entry;

import java.util.ArrayList;
import java.util.List;

public class InMemoryLog implements Log<Entry> {
  private static final int START_INDEX = 0;
  private static final int ROLLBACK = 1;
  private List<Entry> mLogStore;
  private long mIndex = -1;
  private long mCommitIndex = -1;

  public InMemoryLog() {
    mLogStore = new ArrayList<>();
  }

  @Override
  public long append(final Entry entry) {
    Preconditions.checkNotNull(entry, "entry");

    mIndex++;
    mLogStore.add((int) mIndex, entry);

    return mIndex;
  }

  @Override
  public long append(final long index, final Entry entry) {
    Preconditions.checkState(0 <= index && index <= mIndex, "index");
    mLogStore.add((int) index, entry);
    return index;
  }

  @Override
  public Entry read(final long index) {
    try {
      Preconditions.checkState(START_INDEX <= index && index <= mIndex);
      return mLogStore.get((int) index);
    } catch (final Exception e) {
      return null;
    }
  }

  @Override
  public List<Entry> read(final long from, final long to) {
    Preconditions.checkState(START_INDEX <= from && from <= to && to <= mCommitIndex);

    final List<Entry> subList = new ArrayList<>();
    for (int i = (int) from; i <= to; ++i) {
      final Entry entry = read(i);
      if (entry != null) {
        subList.add(entry);
      }
    }

    return ImmutableList.copyOf(subList);
  }

  @Override
  public CommitResult<Entry> commit(final long index) {
    Preconditions.checkState(START_INDEX <= index && index <= mIndex);
    final long oldCommit = mCommitIndex;
    mCommitIndex = index;

    final List<Entry> entries = read(oldCommit + 1, index);

    return new CommitResult<>(entries, index);
  }

  @Override
  public void revert(final long index) {
    Preconditions.checkState(mCommitIndex < index);

    mIndex = index - ROLLBACK;
  }

  @Override
  public long getCommitIndex() {
    return mCommitIndex;
  }

  @Override
  public long getLastIndex() {
    return mIndex;
  }

  @Override
  public String toString() {
    final List<Entry> committed = new ArrayList<>();
    for (int i = START_INDEX; i <= mCommitIndex; ++i) {
      committed.add(mLogStore.get(i));
    }

    return "InMemoryLog{" +
        "mLogStore=" + mLogStore +
        ", mIndex=" + mIndex +
        ", mCommitIndex=" + mCommitIndex +
        '}';
  }
}
