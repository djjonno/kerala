package org.elkd.core.server.converters;

import com.google.common.collect.ImmutableList;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.server.RpcEntry;
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
import static org.mockito.Mockito.*;

public class EntryConverterTest {
  private static final String EVENT = "event";
  private static final String KEY = "key";
  private static final String VALUE = "value";

  private static final SetStateMachineCommand SET_STATE_MACHINE_COMMAND = mock(SetStateMachineCommand.class);
  private static final UnSetStateMachineCommand UNSET_STATE_MACHINE_COMMAND = mock(UnSetStateMachineCommand.class);
  private static final RpcStateMachineCommand RPC_SET_STATE_MACHINE_COMMAND = RpcStateMachineCommand.newBuilder().setKey(KEY).setValue(VALUE).setOperation(SET).build();
  private static final RpcStateMachineCommand RPC_UNSET_STATE_MACHINE_COMMAND = RpcStateMachineCommand.newBuilder().setKey(KEY).setOperation(UNSET).build();

  private static final Entry ENTRY = Entry.builder(EVENT)
      .withCommand(SET_STATE_MACHINE_COMMAND)
      .withCommand(UNSET_STATE_MACHINE_COMMAND)
      .build();
  private static final RpcEntry RPC_ENTRY = RpcEntry.newBuilder().setEvent(EVENT).addAllCommands(ImmutableList.of(
      RPC_SET_STATE_MACHINE_COMMAND,
      RPC_UNSET_STATE_MACHINE_COMMAND
  )).build();

  @Mock ConverterRegistry mConverterRegistry;

  private EntryConverter mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    mUnitUnderTest = new EntryConverter();

    doReturn(SET_STATE_MACHINE_COMMAND).when(mConverterRegistry).convert(RPC_SET_STATE_MACHINE_COMMAND);
    doReturn(RPC_SET_STATE_MACHINE_COMMAND).when(mConverterRegistry).convert(SET_STATE_MACHINE_COMMAND);

    doReturn(UNSET_STATE_MACHINE_COMMAND).when(mConverterRegistry).convert(RPC_UNSET_STATE_MACHINE_COMMAND);
    doReturn(RPC_UNSET_STATE_MACHINE_COMMAND).when(mConverterRegistry).convert(UNSET_STATE_MACHINE_COMMAND);
  }

  @Test
  public void should_convert_Entry() {
    // Given / When
    final RpcEntry response = mUnitUnderTest.convert(ENTRY, mConverterRegistry);

    // Then
    assertEquals(RPC_ENTRY, response);
    verify(mConverterRegistry).convert(SET_STATE_MACHINE_COMMAND);
    verify(mConverterRegistry).convert(UNSET_STATE_MACHINE_COMMAND);
  }

  @Test
  public void should_convert_RpcEntry() {
    // Given / When
    final Entry response = mUnitUnderTest.convert(RPC_ENTRY, mConverterRegistry);

    // Then
    assertEquals(ENTRY, response);
    verify(mConverterRegistry).convert(RPC_SET_STATE_MACHINE_COMMAND);
    verify(mConverterRegistry).convert(RPC_UNSET_STATE_MACHINE_COMMAND);
    verifyNoMoreInteractions(mConverterRegistry);
  }
}
