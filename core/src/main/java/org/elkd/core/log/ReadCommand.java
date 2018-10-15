package org.elkd.core.log;

import com.google.common.base.Preconditions;
import org.elkd.core.log.LogChangeReasons.LogChangeReason;
import org.elkd.core.log.LogChangeReasons.ReadReason;

import javax.annotation.Nonnull;

public class ReadCommand implements LogCommand<Entry> {
  private final long mIndex;
  private final ReadReason mReason;
  private final Log<Entry> mReceiver;

  public ReadCommand(final long index,
                     @Nonnull final Log<Entry> receiver,
                     @Nonnull final ReadReason reason) {
    mIndex = index;
    mReason = Preconditions.checkNotNull(reason, "reason");
    mReceiver = Preconditions.checkNotNull(receiver, "receiver");
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
