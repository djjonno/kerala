package org.elkd.core.consensus.messages;

import com.google.common.base.Preconditions;

public class AppendEntriesResponse {
  private final int mTerm;
  private final boolean mSuccess;

  private AppendEntriesResponse(final Builder builder) {
    Preconditions.checkNotNull(builder, "builder");
    mTerm = builder.mTerm;
    mSuccess = builder.mSuccess;
  }

  public static Builder builder(final int term, final boolean success) {
    return new Builder(term, success);
  }

  public static class Builder {
    private final int mTerm;
    private final boolean mSuccess;

    private Builder(final int term, final boolean success) {
      mTerm = term;
      mSuccess = success;
    }

    public AppendEntriesResponse build() {
      return new AppendEntriesResponse(this);
    }
  }

  public int getTerm() {
    return mTerm;
  }

  public boolean isSuccessful() {
    return mSuccess;
  }

  @Override
  public String toString() {
    return "AppendEntriesResponse{" +
        "mTerm=" + mTerm +
        ", mSuccess=" + mSuccess +
        '}';
  }
}
