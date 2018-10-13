package org.elkd.core.consensus;

public interface AbstractDelegateFactory {
  Delegate getInitialDelegate(Consensus consensus);
  Delegate getDelegate(Consensus consensus, Class<? extends State> klass);
}
