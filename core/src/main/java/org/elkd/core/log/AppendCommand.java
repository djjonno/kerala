package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.elkd.core.log.LogCommandReasons.AppendReason;
import org.elkd.core.log.LogCommandReasons.LogCommandReason;

import javax.annotation.Nonnull;
import java.util.List;

public class AppendCommand implements LogCommand<Long> {
  private final List<Entry> mEntries;
  private final AppendReason mReason;
  private final Log mReceiver;

  public AppendCommand(@Nonnull final ImmutableList<Entry> entries,
                       @Nonnull final AppendReason reason,
                       @Nonnull final Log receiver) {
    mEntries = Preconditions.checkNotNull(entries, "entries");
    mReason = Preconditions.checkNotNull(reason, "reason");
    mReceiver = Preconditions.checkNotNull(receiver, "receiver");
  }

  @Override
  public LogCommandReason getReason() {
    return mReason;
  }

  @Override
  public Long execute() {
    Long index = null;
    for (final Entry entry : mEntries) {
      index = mReceiver.append(entry);
    }

    return index;
  }

  @Override
  public void rollback() {
    // No-op, log is append-only
  }
}
