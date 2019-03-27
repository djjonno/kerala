package org.elkd.core.raft;

public interface AbstractDelegateFactory {
  RaftState getInitialDelegate(Raft raft);
  RaftState getDelegate(Raft raft, Class<? extends State> klass);
}
