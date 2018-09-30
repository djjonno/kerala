package org.elkd.core.log;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventTest {
  private static final String EVENT_TYPE = "eventType";
  private static final String KEY = "key";
  private static final String VAL = "val";

  private Event.Builder mEventBuilder;
  private static final Gson mGson = GsonFactory.getInstance().getGson();

  @Before
  public void setUp() throws Exception {
    mEventBuilder = Event.builder(EVENT_TYPE);
  }

  @Test
  public void should_build_event_with_type() {
    // Given / When
    final Event event = mEventBuilder.build();

    // Then
    assertEquals(EVENT_TYPE, event.getType());
  }

  @Test
  public void should_return_value_given_key() {
    // Given / When
    final Event event = mEventBuilder
        .value(KEY, VAL)
        .build();

    // Then
    assertEquals(EVENT_TYPE, event.getType());
    assertEquals(VAL, event.getValue(KEY));
  }

  @Test
  public void should_set_time_on_creation() {
    // Given / When
    final Event event = mEventBuilder.build();

    // Then
    assertNotNull(event.getTime());
  }

  @Test
  public void should_have_equality_given_consistent_builder_args() {
    // Given / When
    final Event e1 = Event.builder(EVENT_TYPE).value(KEY, VAL).build();
    final Event e2 = Event.builder(EVENT_TYPE).value(KEY, VAL).build();

    // Then
    assertEquals(e1, e2);
  }

  @Test
  public void should_serialize_event_with_values() {
    // Given
    final Event event = mEventBuilder
        .value(KEY, VAL)
        .build();

    // When
    final String serialized = mGson.toJson(event);
    final Event deSerializedEvent = mGson.fromJson(serialized, Event.class);

    // Then
    assertEquals(event, deSerializedEvent);
  }
}
