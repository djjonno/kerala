package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class DefaultStateFactory implements AbstractStateFactory {
  static final Class<? extends State> INITIAL_STATE = FollowerState.class;

  private static Map<Class<? extends State>, State> mStateRegistry;

  public DefaultStateFactory() { }

  @Override
  public State getInitialState(@Nonnull final Consensus consensus) {
    Preconditions.checkNotNull(consensus, "consensus");
    return getState(consensus, INITIAL_STATE);
  }

  @Override
  @Nullable
  public State getState(@Nonnull final Consensus consensus, @Nonnull final Class klass) {
    Preconditions.checkNotNull(consensus, "consensus");
    Preconditions.checkNotNull(klass, "klass");

    if (mStateRegistry == null) {
      mStateRegistry = ImmutableMap.of(
          FollowerState.class, createFollowerState(consensus),
          CandidateState.class, createCandidateState(consensus),
          LeaderState.class, createLeaderState(consensus)
      );
    }

    return mStateRegistry.get(klass);
  }

  private State createFollowerState(@Nonnull final Consensus consensus) {
    return new FollowerState(consensus);
  }

  private State createCandidateState(@Nonnull final Consensus consensus) {
    return new CandidateState(consensus);
  }

  private State createLeaderState(@Nonnull final Consensus consensus) {
    return new LeaderState(consensus);
  }
}
