package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class DefaultStateFactory implements AbstractStateFactory {
  static final Class<? extends RaftState> INITIAL_STATE = RaftFollowerState.class;

  private static Map<Class<? extends RaftState>, RaftState> mStateRegistry;

  @Override
  public RaftState getInitialDelegate(@Nonnull final Raft raft) {
    Preconditions.checkNotNull(raft, "consensus");
    return getState(raft, INITIAL_STATE);
  }

  @Override
  @Nullable
  public RaftState getState(@Nonnull final Raft raft, @Nonnull final Class klass) {
    Preconditions.checkNotNull(raft, "consensus");
    Preconditions.checkNotNull(klass, "klass");

    if (mStateRegistry == null) {
      mStateRegistry = ImmutableMap.of(
          RaftFollowerState.class, createFollowerState(raft),
          RaftCandidateState.class, createCandidateState(raft),
          RaftLeaderState.class, createLeaderState(raft)
      );
    }

    return mStateRegistry.get(klass);
  }

  private RaftState createFollowerState(@Nonnull final Raft raft) {
    return new RaftFollowerState(raft);
  }

  private RaftState createCandidateState(@Nonnull final Raft raft) {
    return new RaftCandidateState(raft);
  }

  private RaftState createLeaderState(@Nonnull final Raft raft) {
    return new RaftLeaderState(raft);
  }
}
