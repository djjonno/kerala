package org.elkd.core.consensus;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class DefaultDelegateFactory implements AbstractDelegateFactory {
  static final Class<? extends Delegate> INITIAL_STATE = FollowerDelegate.class;

  private static Map<Class<? extends Delegate>, Delegate> mStateRegistry;

  public DefaultDelegateFactory() { }

  @Override
  public Delegate getInitialDelegate(@Nonnull final Consensus consensus) {
    Preconditions.checkNotNull(consensus, "consensus");
    return getDelegate(consensus, INITIAL_STATE);
  }

  @Override
  @Nullable
  public Delegate getDelegate(@Nonnull final Consensus consensus, @Nonnull final Class klass) {
    Preconditions.checkNotNull(consensus, "consensus");
    Preconditions.checkNotNull(klass, "klass");

    if (mStateRegistry == null) {
      mStateRegistry = ImmutableMap.of(
          FollowerDelegate.class, createFollowerState(consensus),
          CandidateDelegate.class, createCandidateState(consensus),
          LeaderDelegate.class, createLeaderState(consensus)
      );
    }

    return mStateRegistry.get(klass);
  }

  private Delegate createFollowerState(@Nonnull final Consensus consensus) {
    return new FollowerDelegate(consensus);
  }

  private Delegate createCandidateState(@Nonnull final Consensus consensus) {
    return new CandidateDelegate(consensus);
  }

  private Delegate createLeaderState(@Nonnull final Consensus consensus) {
    return new LeaderDelegate(consensus);
  }
}
