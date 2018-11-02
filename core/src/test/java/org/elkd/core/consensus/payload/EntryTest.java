package org.elkd.core.consensus.payload;

import com.google.common.collect.ImmutableList;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.statemachine.StateMachineCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class EntryTest {
  private static final String EVENT_TYPE = "eventType";

  @Mock StateMachineCommand mStateMachineCommand;

  private Entry.Builder mEventBuilder;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mEventBuilder = Entry.builder(EVENT_TYPE);
  }

  @Test
  public void should_build_entry_with_event() {
    // Given / When
    final Entry entry = mEventBuilder.build();

    // Then
    assertEquals(EVENT_TYPE, entry.getType());
  }

  @Test
  public void should_return_value_given_key() {
    // Given / When
    final Entry entry = mEventBuilder
        .withCommand(mStateMachineCommand)
        .build();

    // Then
    assertEquals(EVENT_TYPE, entry.getType());
    assertEquals(entry.getCommands(), ImmutableList.of(mStateMachineCommand));
  }

  @Test
  public void should_have_equality_given_consistent_builder_args() {
    // Given / When
    final Entry e1 = Entry.builder(EVENT_TYPE).withCommand(mStateMachineCommand).build();
    final Entry e2 = Entry.builder(EVENT_TYPE).withCommand(mStateMachineCommand).build();

    // Then
    assertEquals(e1, e2);
  }
}
