package org.elkd.core.statemachine;

import com.google.common.base.Preconditions;

import java.util.Objects;

public class UnSetStateMachineCommand implements StateMachineCommand {
  private final String mKey;

  public UnSetStateMachineCommand(final String key) {
    mKey = Preconditions.checkNotNull(key, "key");
  }

  @Override
  public void apply(final StateMachine receiver) {
    receiver.unset(mKey);
  }

  @Override
  public String toString() {
    return "UnSet{" + mKey + "}";
  }

  public String getKey() {
    return mKey;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final UnSetStateMachineCommand that = (UnSetStateMachineCommand) o;
    return Objects.equals(mKey, that.mKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mKey);
  }
}
