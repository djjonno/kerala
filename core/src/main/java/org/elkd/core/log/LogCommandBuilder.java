package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.elkd.core.log.LogChangeReasons.LogChangeReason;

public class LogCommandBuilder {
  private final String mCommandName;
  private final LogChangeReason mReason;
  private final ImmutableList.Builder<LogOperation> mOperations = new ImmutableList.Builder<>();

  public static LogCommandBuilder builder(final String commandName, final LogChangeReason reason) {
    return new LogCommandBuilder(commandName, reason);
  }

  public LogCommandBuilder(final String commandName, final LogChangeReason reason) {
    mCommandName = Preconditions.checkNotNull(commandName, "commandName");
    mReason = Preconditions.checkNotNull(reason, "reason");
  }

  public LogCommandBuilder withAppendOperation(final Entry entry) {
    mOperations.add(new AppendOperation(entry));
    return this;
  }

  public LogCommandBuilder withReadOperation(final long index) {
    mOperations.add(new ReadOperation(index));
    return this;
  }

  public LogCommandBuilder withCommitOperation(final long index) {
    mOperations.add(new CommitOperation(index));
    return this;
  }

  public LogCommand build() {
    return LogCommand.of(mCommandName, mReason, mOperations.build());
  }
}
