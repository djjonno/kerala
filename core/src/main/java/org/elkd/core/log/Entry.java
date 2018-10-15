package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.elkd.core.statemachine.StateMachineCommand;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Entry {
  private final String mEvent;
  private final List<StateMachineCommand> mCommands;

  private Entry(@Nonnull final String event,
                @Nonnull final List<StateMachineCommand> stateMachineCommands) {
    mEvent = Preconditions.checkNotNull(event, "event");
    mCommands = ImmutableList.copyOf(Preconditions.checkNotNull(stateMachineCommands, "stateMachineCommands"));
  }

  public static Builder builder(final String event) {
    return new Builder(event);
  }

  public static class Builder {
    private String mEvent;
    private List<StateMachineCommand> mCommands = new ArrayList<>();

    Builder(@Nonnull final String event) {
      mEvent = Preconditions.checkNotNull(event, "event");
    }

    public Builder withCommand(final StateMachineCommand command) {
      mCommands.add(command);
      return this;
    }

    public Entry build() {
      return new Entry(
          mEvent,
          mCommands
      );
    }
  }

  public String getType() {
    return mEvent;
  }

  public List<StateMachineCommand> getCommands() {
    return mCommands;
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
    return Objects.equals(mEvent, entry.mEvent) &&
        Objects.equals(mCommands, entry.mCommands);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mEvent, mCommands);
  }

  @Override
  public String toString() {
    return "Entry{" +
        "mEvent='" + mEvent + '\'' +
        ", mCommands=" + mCommands +
        '}';
  }
}
