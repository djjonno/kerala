package org.elkd.core.consensus;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class LeaderDelegate implements Delegate {
  private static final Logger LOG = Logger.getLogger(CandidateDelegate.class.getName());
  private final Consensus mConsensus;

  LeaderDelegate(@Nonnull final Consensus consensus) {
    mConsensus = Preconditions.checkNotNull(consensus, "consensus");
  }

  @Override
  public void on() {
    LOG.info("online");
  }

  @Override
  public void off() {
    LOG.info("offline");
  }

  @Override
  public AppendEntriesResponse delegateAppendEntries(AppendEntriesRequest request) {
    return null;
  }

  @Override
  public RequestVotesResponse delegateRequestVotes(RequestVotesRequest request) {
    return null;
  }
}
