package org.elkd.core.consensus;

import javax.annotation.Nullable;

public class NodeState {
  private int mCurrentTerm;
  private String mVotedFor;

  public NodeState() {
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
  String getVotedFor() {
    return mVotedFor;
  }

  void setVotedFor(final String votedFor) {
    mVotedFor = votedFor;
    commit();
  }

  private void commit() {
    // TODO: Persist to disk https://elkd-issues.atlassian.net/browse/ELKD-9
  }
}
