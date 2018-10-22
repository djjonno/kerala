package org.elkd.core.statemachine;

import com.google.common.base.Preconditions;

import java.util.Objects;

public class SetStateMachineCommand implements StateMachineCommand {
  private final String mKey;
  private final Object mValue;

  public SetStateMachineCommand(final String key, final Object value) {
    mKey = Preconditions.checkNotNull(key, "key");
    mValue = Preconditions.checkNotNull(value, "value");
  }

  @Override
  public void apply(final StateMachine receiver) {
    receiver.set(mKey, mValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mKey, mValue);
  }

  @Override
  public String toString() {
    return "Set{" + mKey + " -> " + mValue + "}";
  }
}
