package org.elkd.core.raft;

interface State {
  void on();
  void off();
}
