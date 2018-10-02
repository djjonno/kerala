package org.elkd.core.log;

import com.google.common.base.Preconditions;

import java.util.Objects;

public class AppendOperation implements LogOperation {
  private Event mEvent;

  public AppendOperation(final Event event) {
    mEvent = Preconditions.checkNotNull(event, "event");
  }

  @Override
  public LogOperationType getType() {
    return LogOperationType.APPEND;
  }

  public Event getEvent() {
    return mEvent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AppendOperation that = (AppendOperation) o;
    return Objects.equals(mEvent, that.mEvent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mEvent);
  }

  @Override
  public String toString() {
    return "AppendOperation{" +
        "mEvent=" + mEvent +
        '}';
  }
}
