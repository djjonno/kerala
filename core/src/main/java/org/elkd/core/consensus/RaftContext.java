package org.elkd.core.consensus;

import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.Log;

import javax.annotation.Nullable;

public class RaftContext {

  /* persistent state */
  private int mCurrentTerm;
  private String mVotedFor;

  public RaftContext() {
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
    /* write persistent state to disk */
  }
}
