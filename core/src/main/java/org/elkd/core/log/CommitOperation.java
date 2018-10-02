package org.elkd.core.log;

import java.util.Objects;

public class CommitOperation implements LogOperation {
  private final long mIndex;

  public CommitOperation(final long index) {
    mIndex = index;
  }

  @Override
  public LogOperationType getType() {
    return LogOperationType.COMMIT;
  }

  public long getIndex() {
    return mIndex;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CommitOperation that = (CommitOperation) o;
    return mIndex == that.mIndex;
  }

  @Override
  public int hashCode() {

    return Objects.hash(mIndex);
  }

  @Override
  public String toString() {
    return "CommitOperation{" +
        "mIndex=" + mIndex +
        '}';
  }
}
