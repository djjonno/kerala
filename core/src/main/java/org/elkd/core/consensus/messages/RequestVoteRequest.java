package org.elkd.core.consensus.messages;

import com.google.common.base.Preconditions;

public class RequestVoteRequest {
  private final int mTerm;
  private final String mCandidateId;
  private final int mLastLogIndex;
  private final int mLastLogTerm;

  private RequestVoteRequest(final Builder builder) {
    Preconditions.checkNotNull(builder, "builder");
    mTerm = builder.mTerm;
    mCandidateId = builder.mCandidateId;
    mLastLogIndex = builder.mLastLogIndex;
    mLastLogTerm = builder.mLastLogTerm;
  }

  public static Builder builder(final int term,
                                final String candidateId,
                                final int lastLogIndex,
                                final int lastLogTerm) {
    return new Builder(term, candidateId, lastLogIndex, lastLogTerm);
  }

  public static class Builder {
    private final int mTerm;
    private final String mCandidateId;
    private final int mLastLogIndex;
    private final int mLastLogTerm;

    private Builder(final int term,
                    final String candidateId,
                    final int lastLogIndex,
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

  public String getCandidateId() {
    return mCandidateId;
  }

  public int getLastLogIndex() {
    return mLastLogIndex;
  }

  public int getLastLogTerm() {
    return mLastLogTerm;
  }
}
