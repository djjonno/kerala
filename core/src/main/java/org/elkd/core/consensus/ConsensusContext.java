package org.elkd.core.consensus;

public class ConsensusContext {
  private Integer mCurrentTerm = 0;
  private Integer mVotedFor = null;

  public ConsensusContext() { }

  public Integer getCurrentTerm() {
    return mCurrentTerm;
  }

  public void setCurrentTerm(final Integer currentTerm) {
    mCurrentTerm = currentTerm;
    commit();
  }

  public Integer getVotedFor() {
    return mVotedFor;
  }

  public void setVotedFor(final Integer votedFor) {
    mVotedFor = votedFor;
    commit();
  }

  private void commit() {
    // TODO: Persist to disk https://elkd-issues.atlassian.net/browse/ELKD-9
  }
}
