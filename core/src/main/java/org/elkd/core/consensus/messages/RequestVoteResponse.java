package org.elkd.core.consensus.messages;

public class RequestVoteResponse {
  private final int mTerm;
  private final boolean mVoteGranted;

  private RequestVoteResponse(final Builder builder) {
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

    public RequestVoteResponse build() {
      return new RequestVoteResponse(this);
    }
  }

  public int getTerm() {
    return mTerm;
  }

  public boolean isVoteGranted() {
    return mVoteGranted;
  }
}
