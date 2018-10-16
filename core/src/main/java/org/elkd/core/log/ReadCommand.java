package org.elkd.core.log;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class ReadCommand implements LogCommand<Entry> {
  private final long mIndex;
  private final Log<Entry> mReceiver;
  private final LogChangeReason mReason;

  public ReadCommand(final long index,
                     @Nonnull final Log<Entry> receiver,
                     @Nonnull final LogChangeReason reason) {
    mIndex = index;
    mReceiver = Preconditions.checkNotNull(receiver, "receiver");
    mReason = Preconditions.checkNotNull(reason, "reason");
  }

  @Override
  public LogChangeReason getReason() {
    return mReason;
  }

  @Override
  public Entry execute() {
    return mReceiver.read(mIndex);
  }
}
