package org.elkd.core.consensus.payload;

import org.elkd.core.consensus.messages.Entry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class EntryTest {
  private static final int TERM = 1;
  private static final String EVENT_NAME = "eventName";

  private Entry.Builder mEventBuilder;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mEventBuilder = Entry.builder(TERM, EVENT_NAME);
  }

  @Test
  public void should_build_entry_with_event() {
    // Given / When
    final Entry entry = mEventBuilder.build();

    // Then
    assertEquals(TERM, entry.getTerm());
    assertEquals(EVENT_NAME, entry.getEvent());
  }

  @Test
  public void should_return_value_given_key() {
    // Given / When
    final Entry entry = mEventBuilder
        .build();

    // Then
    assertEquals(EVENT_NAME, entry.getEvent());
  }

  @Test
  public void should_have_equality_given_consistent_builder_args() {
    // Given / When
    final Entry e1 = Entry.builder(TERM, EVENT_NAME).build();
    final Entry e2 = Entry.builder(TERM, EVENT_NAME).build();

    // Then
    assertEquals(e1, e2);
  }
}
