package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.elkd.core.consensus.messages.Entry;

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
  public long append(final List<Entry> entries) {
    long index = 0;
    for (Entry entry : entries) {
      index = append(entry);
    }
    return index;
  }

  @Override
  public Entry read(final long index) {
    Preconditions.checkState(START_INDEX <= index && index <= mIndex);
    Preconditions.checkState(index <= mCommitIndex);

    return mLogStore.get((int) index);
  }

  @Override
  public List<Entry> read(final long from, final long to) {
    Preconditions.checkState(START_INDEX <= from && from <= to && to <= mCommitIndex);

    final List<Entry> subList = new ArrayList<>();
    for (int i = (int) from; i <= to; ++i) {
      subList.add(mLogStore.get(i));
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
  public String toString() {
    final List<Entry> committed = new ArrayList<>();
    for (int i = START_INDEX; i <= mCommitIndex; ++i) {
      committed.add(mLogStore.get(i));
    }

    return "InMemoryLog{" +
        "mLogStore=" + committed +
        ", mIndex=" + mIndex +
        ", mCommitIndex=" + mCommitIndex +
        '}';
  }
}
