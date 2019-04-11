package org.elkd.core.consensus;

public interface AbstractStateFactory {
  RaftState getInitialDelegate(Raft raft);
  RaftState getState(Raft raft, Class<? extends State> klass);
}
