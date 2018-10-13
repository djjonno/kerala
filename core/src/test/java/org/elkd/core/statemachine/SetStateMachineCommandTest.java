package org.elkd.core.statemachine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class SetStateMachineCommandTest {
  private static final String KEY = "key";
  private static final String VAL = "val";

  @Mock StateMachine mReceiver;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void should_apply_command_on_receiver() {
    // Given
    final SetStateMachineCommand command = new SetStateMachineCommand(
        KEY, VAL
    );

    // When
    command.apply(mReceiver);

    // Then
    verify(mReceiver).set(KEY, VAL);
  }
}
