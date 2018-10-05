package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.elkd.core.log.LogChangeReasons.LogChangeReason;

import java.util.Objects;

public class LogCommand {
  private final String mCommandName;
  private final LogChangeReason mReason;
  private final ImmutableList<LogOperation> mOperations;

  private LogCommand(final String commandName,
                     final LogChangeReason reason,
                     final ImmutableList<LogOperation> operations) {
    mCommandName = Preconditions.checkNotNull(commandName, "commandName");
    mReason = Preconditions.checkNotNull(reason, "reason");
    mOperations = Preconditions.checkNotNull(operations, "operations");
  }

  public ImmutableList<LogOperation> getOperations() {
    return mOperations;
  }

  public String getCommandName() {
    return mCommandName;
  }

  public LogChangeReason getReason() {
    return mReason;
  }

  public static LogCommand of(final String commandName,
                              final LogChangeReason reason,
                              final LogOperation operation) {
    return new LogCommand(commandName, reason, ImmutableList.of(operation));
  }

  public static LogCommand of(final String commandName,
                              final LogChangeReason reason,
                              final LogOperation... operations) {
    return new LogCommand(commandName, reason, ImmutableList.copyOf(operations));
  }

  public static LogCommand of(final String commandName,
                              final LogChangeReason reason,
                              final ImmutableList<LogOperation> operations) {
    return new LogCommand(commandName, reason, ImmutableList.copyOf(operations));
  }
  @Override
  public boolean equals(final Object rhs) {
    if (this == rhs) {
      return true;
    }
    if (rhs == null || getClass() != rhs.getClass()) {
      return false;
    }
    final LogCommand that = (LogCommand) rhs;
    return Objects.equals(mCommandName, that.mCommandName) &&
        Objects.equals(mOperations, that.mOperations) &&
        Objects.equals(mReason, that.mReason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mCommandName, mOperations, mReason);
  }

  @Override
  public String toString() {
    return "LogCommand{" +
        "mCommandName='" + mCommandName + '\'' +
        ", mOperations=" + mOperations +
        ", mReason=" + mReason +
        '}';
  }
}
