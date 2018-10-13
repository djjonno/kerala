package org.elkd.core.statemachine;

public interface StateMachineCommand {
  void apply(StateMachine receiver);
}
