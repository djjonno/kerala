package org.elkd.core.consensus.messages;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Entry {
  public static final Entry NULL_ENTRY = Entry.builder(0, "default").build();

  private final int mTerm;
  private final String mEvent;

  private Entry(final int term, @Nonnull final String event) {
    mTerm = term;
    mEvent = Preconditions.checkNotNull(event, "event");
  }

  public static Builder builder(final int term, final String event) {
    return new Builder(term, event);
  }

  public static class Builder {
    private int mTerm;
    private String mEvent;

    Builder(final int term, @Nonnull final String event) {
      mTerm = term;
      mEvent = Preconditions.checkNotNull(event, "event");
    }

    public Entry build() {
      return new Entry(
          mTerm,
          mEvent
      );
    }
  }

  public int getTerm() {
    return mTerm;
  }

  public String getEvent() {
    return mEvent;
  }

  @Override
  public boolean equals(final Object rhs) {
    if (this == rhs) {
      return true;
    }
    if (rhs == null || getClass() != rhs.getClass()) {
      return false;
    }
    final Entry entry = (Entry) rhs;
    return mTerm == entry.mTerm &&
        Objects.equals(mEvent, entry.mEvent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mTerm, mEvent);
  }

  @Override
  public String toString() {
    return "Entry{" +
        "mTerm=" + mTerm +
        ", mEvent='" + mEvent + '\'' +
        '}';
  }
}
