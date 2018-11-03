package org.elkd.core.server.converters;

import com.google.common.collect.ImmutableList;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.server.RpcAppendEntriesRequest;
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
import static org.mockito.Mockito.doReturn;

public class AppendEntriesRequestConverterTest {

  private static final int TERM = 1;
  private static final int PREV_LOG_TERM = 2;
  private static final int PREV_LOG_INDEX = 3;
  private static final String LEADER_ID = "leaderId";
  private static final int LEADER_COMMIT = 4;
  private static final String EVENT_NAME = "event";
  private static final String KEY = "key";
  private static final String VALUE = "value";
  private static final Entry ENTRY = Entry.builder(EVENT_NAME)
      .withCommand(new SetStateMachineCommand(KEY, VALUE))
      .withCommand(new UnSetStateMachineCommand(KEY))
      .build();
  private static final RpcEntry RPC_ENTRY = RpcEntry.newBuilder()
      .setEvent(EVENT_NAME)
      .addAllCommands(ImmutableList.of(
          RpcStateMachineCommand.newBuilder().setKey(KEY).setValue(VALUE).setOperation(SET).build(),
          RpcStateMachineCommand.newBuilder().setKey(KEY).setOperation(UNSET).build()
      )).build();

  private static final AppendEntriesRequest APPEND_ENTRIES_REQUEST = AppendEntriesRequest.builder(
      TERM,
      PREV_LOG_TERM,
      PREV_LOG_INDEX,
      LEADER_ID,
      LEADER_COMMIT
  ).withEntry(ENTRY).build();

  private static final RpcAppendEntriesRequest RPC_APPEND_ENTRIES_REQUEST = RpcAppendEntriesRequest.newBuilder()
      .setTerm(TERM)
      .setPrevLogTerm(PREV_LOG_TERM)
      .setPrevLogIndex(PREV_LOG_INDEX)
      .setLeaderId(LEADER_ID)
      .setLeaderCommit(LEADER_COMMIT)
      .addAllEntries(ImmutableList.of(RPC_ENTRY))
      .build();

  @Mock ConverterRegistry mConverterRegistry;

  private AppendEntriesRequestConverter mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    mUnitUnderTest = new AppendEntriesRequestConverter();

    doReturn(RPC_ENTRY)
        .when(mConverterRegistry)
        .convert(ENTRY);
    doReturn(ENTRY)
        .when(mConverterRegistry)
        .convert(RPC_ENTRY);
  }

  @Test
  public void should_convert_AppendEntriesRequest() {
    // Given / When
    RpcAppendEntriesRequest request = mUnitUnderTest.convert(APPEND_ENTRIES_REQUEST, mConverterRegistry);

    // Then
    assertEquals(TERM, request.getTerm());
    assertEquals(PREV_LOG_TERM, request.getPrevLogTerm());
    assertEquals(PREV_LOG_INDEX, request.getPrevLogIndex());
    assertEquals(LEADER_ID, request.getLeaderId());
    assertEquals(LEADER_COMMIT, request.getLeaderCommit());
    assertEquals(RPC_ENTRY, request.getEntriesList().get(0));
  }

  @Test
  public void should_convert_RpcAppendEntriesRequest() {
    // Given / When
    AppendEntriesRequest request = mUnitUnderTest.convert(RPC_APPEND_ENTRIES_REQUEST, mConverterRegistry);

    // Then
    assertEquals(TERM, request.getTerm());
    assertEquals(PREV_LOG_TERM, request.getPrevLogTerm());
    assertEquals(PREV_LOG_INDEX, request.getPrevLogIndex());
    assertEquals(LEADER_ID, request.getLeaderId());
    assertEquals(LEADER_COMMIT, request.getLeaderCommit());
    assertEquals(ENTRY, request.getEntries().get(0));
  }
}
