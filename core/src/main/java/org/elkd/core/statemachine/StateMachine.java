package org.elkd.core.statemachine;

public interface StateMachine {
  void set(String key, Object value);

  void unset(String key);

  Object get(String key);
}
