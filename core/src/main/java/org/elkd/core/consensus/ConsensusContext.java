package org.elkd.core.consensus;

import javax.annotation.Nullable;

public class ConsensusContext {
  private int mCurrentTerm;
  private Integer mVotedFor;

  public ConsensusContext() {
    mCurrentTerm = 0;
    mVotedFor = null;
  }

  int getCurrentTerm() {
    return mCurrentTerm;
  }

  void setCurrentTerm(final int currentTerm) {
    mCurrentTerm = currentTerm;
    commit();
  }

  @Nullable
  Integer getVotedFor() {
    return mVotedFor;
  }

  void setVotedFor(final Integer votedFor) {
    mVotedFor = votedFor;
    commit();
  }

  private void commit() {
    // TODO: Persist to disk https://elkd-issues.atlassian.net/browse/ELKD-9
  }
}
