package org.elkd.core.log;

import com.google.common.base.Preconditions;
import org.elkd.core.log.LogChangeReasons.CommitReason;
import org.elkd.core.log.LogChangeReasons.LogChangeReason;

import javax.annotation.Nonnull;

public class CommitCommand implements LogCommand<CommitResult<Entry>> {
  private final long mIndex;
  private final LogChangeReason mReason;
  private final Log<Entry> mReceiver;

  public CommitCommand(final long index,
                       @Nonnull final Log<Entry> receiver,
                       @Nonnull final CommitReason reason) {
    mIndex = index;
    mReason = Preconditions.checkNotNull(reason, "reason");
    mReceiver = Preconditions.checkNotNull(receiver, "receiver");
  }

  @Override
  public LogChangeReason getReason() {
    return mReason;
  }

  @Override
  public CommitResult<Entry> execute() {
    return mReceiver.commit(mIndex);
  }
}
