package org.elkd.core.log;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppendOperationTest {
  private static final String EVENT_TYPE_1 = "eventType1";
  private static final String EVENT_TYPE_2 = "eventType2";

  @Test
  public void should_return_event() {
    // Given
    final Entry entry = Entry.builder(EVENT_TYPE_1).build();

    // When
    final AppendOperation operation = new AppendOperation(entry);

    // Then
    assertEquals(entry, operation.getEntry());
    assertEquals(EVENT_TYPE_1, operation.getEntry().getType());
  }

  @Test(expected = NullPointerException.class)
  public void should_throw_exception_with_null_event() {
    // Given / When
    new AppendOperation(null);

    // Then - exception thrown
  }

  @Test
  public void should_return_append_operation_type() {
    // Given / When
    final Entry entry = Entry.builder(EVENT_TYPE_1).build();
    final AppendOperation operation = new AppendOperation(entry);
    
    // Then
    assertEquals(LogOperationType.APPEND, operation.getType());
  }

  @Test
  public void should_be_equivalent_with_same_event_object() {
    // Given
    final Entry entry = Entry.builder(EVENT_TYPE_1).build();

    // When
    final AppendOperation first = new AppendOperation(entry);
    final AppendOperation second = new AppendOperation(entry);

    // Then
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  public void should_not_be_equivalent_with_different_event_object() {
    // Given
    final Entry firstEntry = Entry.builder(EVENT_TYPE_1).build();
    final Entry secondEntry = Entry.builder(EVENT_TYPE_2).build();

    // When
    final AppendOperation first = new AppendOperation(firstEntry);
    final AppendOperation second = new AppendOperation(secondEntry);

    // Then
    assertNotEquals(first, second);
    assertNotEquals(first.hashCode(), second.hashCode());
  }
}
