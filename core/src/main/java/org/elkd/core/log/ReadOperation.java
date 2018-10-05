package org.elkd.core.log;

import java.util.Objects;

public class ReadOperation implements LogOperation {
  private final long mIndex;

  public ReadOperation(final long index) {
    mIndex = index;
  }

  @Override
  public LogOperationType getType() {
    return LogOperationType.READ;
  }

  public long getIndex() {
    return mIndex;
  }

  @Override
  public boolean equals(final Object rhs) {
    if (this == rhs) {
      return true;
    }
    if (rhs == null || getClass() != rhs.getClass()) {
      return false;
    }
    final ReadOperation that = (ReadOperation) rhs;
    return mIndex == that.mIndex;
  }

  @Override
  public int hashCode() {
    return Objects.hash(mIndex);
  }

  @Override
  public String toString() {
    return "ReadOperation{" +
        "mIndex=" + mIndex +
        '}';
  }
}
