package org.elkd.core.consensus.messages;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AppendEntriesRequest {
  private final int mTerm;
  private final int mPrevLogTerm;
  private final int mPrevLogIndex;
  private final String mLeaderId;
  private final int mLeaderCommit;
  private final List<Entry> mEntries;

  private AppendEntriesRequest(final Builder builder) {
    Preconditions.checkNotNull(builder, "builder");
    mTerm = builder.mTerm;
    mPrevLogTerm = builder.mPrevLogTerm;
    mPrevLogIndex = builder.mPrevLogIndex;
    mLeaderId = builder.mLeaderId;
    mLeaderCommit = builder.mLeaderCommit;
    mEntries = ImmutableList.copyOf(builder.mEntries);
  }

  public static Builder builder(final int term,
                                final int prevLogTerm,
                                final int prevLogIndex,
                                final String leaderId,
                                final int leaderCommit) {
    return new Builder(term, prevLogTerm, prevLogIndex, leaderId, leaderCommit);
  }

  public static class Builder {
    private final int mTerm;
    private final int mPrevLogTerm;
    private final int mPrevLogIndex;
    private final String mLeaderId;
    private final int mLeaderCommit;
    private final List<Entry> mEntries = new ArrayList<>();

    private Builder(final int term,
                    final int prevLogTerm,
                    final int prevLogIndex,
                    final String leaderId,
                    final int leaderCommit) {
      mTerm = term;
      mLeaderId = leaderId;
      mPrevLogTerm = prevLogTerm;
      mPrevLogIndex = prevLogIndex;
      mLeaderCommit = leaderCommit;
    }

    public Builder withEntry(@Nonnull final Entry entry) {
      return withEntries(ImmutableList.of(entry));
    }

    public Builder withEntries(@Nonnull final List<Entry> entries) {
      Preconditions.checkNotNull(entries, "entries");
      mEntries.addAll(entries);
      return this;
    }

    public AppendEntriesRequest build() {
      return new AppendEntriesRequest(this);
    }
  }

  public int getTerm() {
    return mTerm;
  }

  public int getPrevLogTerm() {
    return mPrevLogTerm;
  }

  public int getPrevLogIndex() {
    return mPrevLogIndex;
  }

  public String getLeaderId() {
    return mLeaderId;
  }

  public int getLeaderCommit() {
    return mLeaderCommit;
  }

  public List<Entry> getEntries() {
    return mEntries;
  }
}
