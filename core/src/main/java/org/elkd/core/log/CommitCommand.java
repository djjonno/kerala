package org.elkd.core.log;

import com.google.common.base.Preconditions;
import org.elkd.core.log.LogCommandReasons.CommitReason;
import org.elkd.core.log.LogCommandReasons.LogCommandReason;

import javax.annotation.Nonnull;

public class CommitCommand implements LogCommand<Void> {
  private final long mIndex;
  private final LogCommandReason mReason;
  private final Log mReceiver;

  public CommitCommand(@Nonnull final long index,
                       @Nonnull final CommitReason reason,
                       @Nonnull final Log receiver) {
    mIndex = Preconditions.checkNotNull(index, "index");
    mReason = Preconditions.checkNotNull(reason, "reason");
    mReceiver = Preconditions.checkNotNull(receiver, "receiver");
  }

  @Override
  public LogCommandReason getReason() {
    return mReason;
  }

  @Override
  public Void execute() {
    mReceiver.commit(mIndex);
    return null;
  }

  @Override
  public void rollback() {
    mReceiver.rollback(mIndex);
  }
}
