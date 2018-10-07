package org.elkd.core.log;

import com.google.common.base.Preconditions;
import org.elkd.core.log.LogCommandReasons.LogCommandReason;
import org.elkd.core.log.LogCommandReasons.ReadReason;

import javax.annotation.Nonnull;

public class ReadCommand implements LogCommand<Entry> {
  private final long mIndex;
  private final ReadReason mReason;
  private final Log<Entry> mReceiver;

  public ReadCommand(final long index,
                     @Nonnull final ReadReason reason,
                     @Nonnull final Log<Entry> receiver) {
    mIndex = index;
    mReason = Preconditions.checkNotNull(reason, "reason");
    mReceiver = Preconditions.checkNotNull(receiver, "receiver");
  }

  @Override
  public LogCommandReason getReason() {
    return mReason;
  }

  @Override
  public Entry execute() {
    return mReceiver.read(mIndex);
  }
}
