package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.log.LogInvoker;

import javax.annotation.Nullable;

public class NodeProperties {

  /* persistent state */
  private int mCurrentTerm;
  private String mVotedFor;
  private LogInvoker<Entry> mLogInvoker;

  public NodeProperties(final LogInvoker<Entry> logInvoker) {
    mCurrentTerm = 0;
    mVotedFor = null;
    mLogInvoker = Preconditions.checkNotNull(logInvoker, "logInvoker");
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

  public LogInvoker<Entry> getLogInvoker() {
    return mLogInvoker;
  }

  public long getCommitIndex() {
    return mLogInvoker.getCommitIndex();
  }

  private void commit() {
    // TODO: Persist to disk https://elkd-issues.atlassian.net/browse/ELKD-9
    /* write persistent state to disk */
  }
}