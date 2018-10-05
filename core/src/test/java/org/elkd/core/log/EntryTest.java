package org.elkd.core.log;

import com.google.gson.Gson;
import org.elkd.core.server.GsonFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EntryTest {
  private static final String EVENT_TYPE = "eventType";
  private static final String KEY = "key";
  private static final String VAL = "val";
  private static final Gson GSON = GsonFactory.getInstance().getGson();

  private Entry.Builder mEventBuilder;

  @Before
  public void setUp() throws Exception {
    mEventBuilder = Entry.builder(EVENT_TYPE);
  }

  @Test
  public void should_build_event_with_type() {
    // Given / When
    final Entry entry = mEventBuilder.build();

    // Then
    assertEquals(EVENT_TYPE, entry.getType());
  }

  @Test
  public void should_return_value_given_key() {
    // Given / When
    final Entry entry = mEventBuilder
        .value(KEY, VAL)
        .build();

    // Then
    assertEquals(EVENT_TYPE, entry.getType());
    assertEquals(VAL, entry.getValue(KEY));
  }

  @Test
  public void should_set_time_on_creation() {
    // Given / When
    final Entry entry = mEventBuilder.build();

    // Then
    assertNotNull(entry.getTime());
  }

  @Test
  public void should_have_equality_given_consistent_builder_args() {
    // Given / When
    final Entry e1 = Entry.builder(EVENT_TYPE).value(KEY, VAL).build();
    final Entry e2 = Entry.builder(EVENT_TYPE).value(KEY, VAL).build();

    // Then
    assertEquals(e1, e2);
  }

  @Test
  public void should_serialize_event_with_values() {
    // Given
    final Entry entry = mEventBuilder
        .value(KEY, VAL)
        .build();

    // When
    final String serialized = GSON.toJson(entry);
    final Entry deSerializedEntry = GSON.fromJson(serialized, Entry.class);

    // Then
    assertEquals(entry, deSerializedEntry);
  }
}
