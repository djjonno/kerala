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
  public int hashCode() {
    return Objects.hash(mKey);
  }

  @Override
  public String toString() {
    return "UnSet{" + mKey + "}";
  }
}
