package org.elkd.core.consensus.messages;

public class RequestVotesResponse {
  private final int mTerm;
  private final boolean mVoteGranted;

  private RequestVotesResponse(final Builder builder) {
    mTerm = builder.mTerm;
    mVoteGranted = builder.mVoteGranted;
  }

  public static Builder builder(final int term, final boolean voteGranted) {
    return new Builder(term, voteGranted);
  }

  public static class Builder {
    private final int mTerm;
    private final boolean mVoteGranted;

    private Builder(final int term, final boolean voteGranted) {
      mTerm = term;
      mVoteGranted = voteGranted;
    }

    public RequestVotesResponse build() {
      return new RequestVotesResponse(this);
    }
  }

  public int getTerm() {
    return mTerm;
  }

  public boolean isVoteGranted() {
    return mVoteGranted;
  }
}
