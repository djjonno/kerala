package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.elkd.core.log.LogChangeReasons.AppendReason;
import org.elkd.core.log.LogChangeReasons.LogChangeReason;

import javax.annotation.Nonnull;
import java.util.List;

public class AppendCommand implements LogCommand<Long> {
  private final List<Entry> mEntries;
  private final AppendReason mReason;
  private final Log<Entry> mReceiver;

  public AppendCommand(@Nonnull final Entry entry,
                       @Nonnull final Log<Entry> receiver,
                       @Nonnull final AppendReason reason) {
    this(ImmutableList.of(entry), receiver, reason);
  }

  public AppendCommand(@Nonnull final ImmutableList<Entry> entries,
                       @Nonnull final Log<Entry> receiver,
                       @Nonnull final AppendReason reason) {
    mEntries = Preconditions.checkNotNull(entries, "entries");
    mReason = Preconditions.checkNotNull(reason, "reason");
    mReceiver = Preconditions.checkNotNull(receiver, "receiver");
  }

  @Override
  public LogChangeReason getReason() {
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
}
