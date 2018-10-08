package org.elkd.core.consensus;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class FollowerState implements State {
  private static final Logger LOG = Logger.getLogger(FollowerState.class.getName());
  private final Consensus mConsensus;

  public FollowerState(@Nonnull final Consensus consensus) {
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
  public AppendEntriesResponse handleAppendEntries(final AppendEntriesRequest request) {
    return null;
  }

  @Override
  public RequestVotesResponse handleRequestVotes(final RequestVotesRequest request) {
    return null;
  }
}
