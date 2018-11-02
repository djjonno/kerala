package org.elkd.core.log.commands;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.elkd.core.log.CommitResult;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.Log;
import org.elkd.core.log.LogChangeReason;

import javax.annotation.Nonnull;
import java.util.List;

public class AppendAndCommitCommand implements LogCommand<CommitResult<Entry>> {
  private final ImmutableList<Entry> mEntries;
  private final Log<Entry> mReceiver;
  private final LogChangeReason mReason;

  public AppendAndCommitCommand(@Nonnull final List<Entry> entries,
                                @Nonnull final Log<Entry> receiver,
                                @Nonnull final LogChangeReason reason) {
    mEntries = ImmutableList.copyOf(Preconditions.checkNotNull(entries, "entries"));
    mReceiver = Preconditions.checkNotNull(receiver, "receiver");
    mReason = Preconditions.checkNotNull(reason, "reason");
  }

  @Override
  public LogChangeReason getReason() {
    return mReason;
  }

  @Override
  public CommitResult<Entry> execute() {
    final AppendCommand appendCommand = new AppendCommand(mEntries, mReceiver, mReason);
    final Long transaction = appendCommand.execute();
    final CommitCommand commitCommand = new CommitCommand(transaction, mReceiver, mReason);
    return commitCommand.execute();
  }
}
