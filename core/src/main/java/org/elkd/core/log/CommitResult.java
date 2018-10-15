package org.elkd.core.log;

import java.util.List;

public class CommitResult<T> {
  private final List<T> mCommitted;
  private final long mCommitIndex;

  public CommitResult(final List<T> committed, final long commitIndex) {
    mCommitted = committed;
    mCommitIndex = commitIndex;
  }

  public List<T> getCommitted() {
    return mCommitted;
  }

  public long getCommitIndex() {
    return mCommitIndex;
  }
}
