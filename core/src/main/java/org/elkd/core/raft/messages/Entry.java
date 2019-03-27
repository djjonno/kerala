package org.elkd.core.raft.messages;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.elkd.core.statemachine.StateMachineCommand;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Entry {
  private final int mTerm;
  private final String mEvent;
  private final List<StateMachineCommand> mCommands;

  private Entry(final int term,
                @Nonnull final String event,
                @Nonnull final List<StateMachineCommand> stateMachineCommands) {
    mTerm = term;
    mEvent = Preconditions.checkNotNull(event, "event");
    mCommands = ImmutableList.copyOf(Preconditions.checkNotNull(stateMachineCommands, "stateMachineCommands"));
  }

  public static Builder builder(final int term, final String event) {
    return new Builder(term, event);
  }

  public static class Builder {
    private int mTerm;
    private String mEvent;
    private List<StateMachineCommand> mCommands = new ArrayList<>();

    Builder(final int term, @Nonnull final String event) {
      mTerm = term;
      mEvent = Preconditions.checkNotNull(event, "event");
    }

    public Builder withCommand(final StateMachineCommand command) {
      mCommands.add(command);
      return this;
    }

    public Entry build() {
      return new Entry(
          mTerm,
          mEvent,
          mCommands
      );
    }
  }

  public int getTerm() {
    return mTerm;
  }

  public String getEvent() {
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
    return mTerm == entry.mTerm &&
        Objects.equals(mEvent, entry.mEvent) &&
        Objects.equals(mCommands, entry.mCommands);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mTerm, mEvent, mCommands);
  }

  @Override
  public String toString() {
    return "Entry{" +
        "mTerm=" + mTerm +
        ", mEvent='" + mEvent + '\'' +
        ", mCommands=" + mCommands +
        '}';
  }
}
