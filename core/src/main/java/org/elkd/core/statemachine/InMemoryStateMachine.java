package org.elkd.core.statemachine;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class InMemoryStateMachine implements StateMachine {
  private final Map<String, Object> mState = new HashMap<>();

  public InMemoryStateMachine() {
  }

  @Override
  public void set(final String key, final Object value) {
    mState.put(key, value);
  }

  @Override
  public void unset(final String key) {
    mState.remove(key);
  }

  @Override
  @Nullable
  public Object get(final String key) {
    return mState.get(key);
  }
}
