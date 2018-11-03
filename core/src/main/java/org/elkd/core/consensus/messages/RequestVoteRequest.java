package org.elkd.core.consensus.messages;

import com.google.common.base.Preconditions;

public class RequestVoteRequest {
  private final int mTerm;
  private final int mCandidateId;
  private final long mLastLogIndex;
  private final int mLastLogTerm;

  private RequestVoteRequest(final Builder builder) {
    Preconditions.checkNotNull(builder, "builder");
    mTerm = builder.mTerm;
    mCandidateId = builder.mCandidateId;
    mLastLogIndex = builder.mLastLogIndex;
    mLastLogTerm = builder.mLastLogTerm;
  }

  public static Builder builder(final int term,
                                final int candidateId,
                                final long lastLogIndex,
                                final int lastLogTerm) {
    return new Builder(term, candidateId, lastLogIndex, lastLogTerm);
  }

  public static class Builder {
    private final int mTerm;
    private final int mCandidateId;
    private final long mLastLogIndex;
    private final int mLastLogTerm;

    private Builder(final int term,
                    final int candidateId,
                    final long lastLogIndex,
                    final int lastLogTerm) {
      mTerm = term;
      mCandidateId = candidateId;
      mLastLogIndex = lastLogIndex;
      mLastLogTerm = lastLogTerm;
    }

    public RequestVoteRequest build() {
      return new RequestVoteRequest(this);
    }
  }

  public int getTerm() {
    return mTerm;
  }

  public int getCandidateId() {
    return mCandidateId;
  }

  public long getLastLogIndex() {
    return mLastLogIndex;
  }

  public int getLastLogTerm() {
    return mLastLogTerm;
  }
}
