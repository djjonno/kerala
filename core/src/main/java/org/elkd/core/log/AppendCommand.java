package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.List;

public class AppendCommand implements LogCommand<Long> {
  private final List<Entry> mEntries;
  private final Log<Entry> mReceiver;
  private final LogChangeReason mReason;

  public AppendCommand(@Nonnull final Entry entry,
                       @Nonnull final Log<Entry> receiver,
                       @Nonnull final LogChangeReason reason) {
    this(ImmutableList.of(entry), receiver, reason);
  }

  public AppendCommand(@Nonnull final ImmutableList<Entry> entries,
                       @Nonnull final Log<Entry> receiver,
                       @Nonnull final LogChangeReason reason) {
    mEntries = Preconditions.checkNotNull(entries, "entries");
    mReceiver = Preconditions.checkNotNull(receiver, "receiver");
    mReason = Preconditions.checkNotNull(reason, "reason");
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
