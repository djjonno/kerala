package org.elkd.core.statemachine;

import com.google.common.base.Preconditions;

import java.util.Objects;

public class SetStateMachineCommand implements StateMachineCommand {
  private final String mKey;
  private final String mValue;

  public SetStateMachineCommand(final String key, final String value) {
    mKey = Preconditions.checkNotNull(key, "key");
    mValue = Preconditions.checkNotNull(value, "value");
  }

  @Override
  public void apply(final StateMachine receiver) {
    receiver.set(mKey, mValue);
  }

  @Override
  public String toString() {
    return "Set{" + mKey + " -> " + mValue + "}";
  }

  public String getKey() {
    return mKey;
  }

  public String getValue() {
    return mValue;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final SetStateMachineCommand that = (SetStateMachineCommand) o;
    return Objects.equals(mKey, that.mKey) &&
        Objects.equals(mValue, that.mValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mKey, mValue);
  }
}
