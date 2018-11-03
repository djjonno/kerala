package org.elkd.core.server.converters;

import org.elkd.core.server.RpcStateMachineCommand;
import org.elkd.core.statemachine.SetStateMachineCommand;
import org.elkd.core.statemachine.UnSetStateMachineCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.elkd.core.server.RpcStateMachineCommand.Operation.SET;
import static org.elkd.core.server.RpcStateMachineCommand.Operation.UNSET;
import static org.junit.Assert.assertEquals;

public class StateMachineCommandConverterTest {
  private static final String KEY = "key";
  private static final String VALUE = "value";

  private static final SetStateMachineCommand SET_STATE_MACHINE_COMMAND = new SetStateMachineCommand(KEY, VALUE);
  private static final UnSetStateMachineCommand UNSET_STATE_MACHINE_COMMAND = new UnSetStateMachineCommand(KEY);
  private static final RpcStateMachineCommand RPC_SET_STATE_MACHINE_COMMAND = RpcStateMachineCommand.newBuilder().setOperation(SET).setKey(KEY).setValue(VALUE).build();
  private static final RpcStateMachineCommand RPC_UNSET_STATE_MACHINE_COMMAND = RpcStateMachineCommand.newBuilder().setOperation(UNSET).setKey(KEY).build();

  @Mock ConverterRegistry mConverterRegistry;

  private StateMachineCommandConverter mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    mUnitUnderTest = new StateMachineCommandConverter();
  }

  @Test
  public void should_convert_SetStateMachineCommand() {
    // Given / When
    final RpcStateMachineCommand response = mUnitUnderTest.convert(SET_STATE_MACHINE_COMMAND, mConverterRegistry);

    // Then
    assertEquals(RPC_SET_STATE_MACHINE_COMMAND, response);
  }

  @Test
  public void should_convert_RpcSetStateMachineCommand() {
    // Given / When
    final SetStateMachineCommand response = mUnitUnderTest.convert(RPC_SET_STATE_MACHINE_COMMAND, mConverterRegistry);

    // Then
    assertEquals(SET_STATE_MACHINE_COMMAND, response);
  }

  @Test
  public void should_convert_UnSetStateMachineCommand() {
    // Given / When
    final RpcStateMachineCommand response = mUnitUnderTest.convert(UNSET_STATE_MACHINE_COMMAND, mConverterRegistry);

    // Then
    assertEquals(RPC_UNSET_STATE_MACHINE_COMMAND, response);
  }

  @Test
  public void should_convert_RpcUnSetStateMachineCommand() {
    // Given / When
    final UnSetStateMachineCommand response = mUnitUnderTest.convert(RPC_UNSET_STATE_MACHINE_COMMAND, mConverterRegistry);

    // Then
    assertEquals(UNSET_STATE_MACHINE_COMMAND, response);
  }
}
