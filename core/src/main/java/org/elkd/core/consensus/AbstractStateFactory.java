package org.elkd.core.consensus;

public interface AbstractStateFactory {
  State getInitialState(Consensus consensus);
  State getState(Consensus consensus, Class<? extends State> klass);
}
