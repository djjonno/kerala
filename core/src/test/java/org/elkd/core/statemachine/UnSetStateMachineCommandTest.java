package org.elkd.core.statemachine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class UnSetStateMachineCommandTest {
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
    final UnSetStateMachineCommand command = new UnSetStateMachineCommand(
        KEY
    );

    // When
    command.apply(mReceiver);

    // Then
    verify(mReceiver).unset(KEY);
  }
}
