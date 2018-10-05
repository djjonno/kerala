package org.elkd.core.log;

import com.google.common.base.Preconditions;

import java.util.Objects;

public class AppendOperation implements LogOperation {
  private Entry mEntry;

  public AppendOperation(final Entry entry) {
    mEntry = Preconditions.checkNotNull(entry, "entry");
  }

  @Override
  public LogOperationType getType() {
    return LogOperationType.APPEND;
  }

  public Entry getEntry() {
    return mEntry;
  }

  @Override
  public boolean equals(final Object rhs) {
    if (this == rhs) {
      return true;
    }
    if (rhs == null || getClass() != rhs.getClass()) {
      return false;
    }
    final AppendOperation that = (AppendOperation) rhs;
    return Objects.equals(mEntry, that.mEntry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mEntry);
  }

  @Override
  public String toString() {
    return "AppendOperation{" +
        "mEntry=" + mEntry +
        '}';
  }
}
