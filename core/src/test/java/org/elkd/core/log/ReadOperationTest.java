package org.elkd.core.log;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ReadOperationTest {
  private static final int INDEX_1 = 1;
  private static final int INDEX_2 = 2;

  @Test
  public void should_return_index() {
    // Given / When
    final ReadOperation operation = new ReadOperation(INDEX_1);

    // Then
    assertEquals(INDEX_1, operation.getIndex());
  }

  @Test
  public void should_return_read_operation_type() {
    // Given / When
    final ReadOperation operation = new ReadOperation(INDEX_1);

    // Then
    assertEquals(LogOperationType.READ, operation.getType());
  }

  @Test
  public void should_be_equivalent_with_same_index_value() {
    // Given / When
    final ReadOperation first = new ReadOperation(INDEX_1);
    final ReadOperation second = new ReadOperation(INDEX_1);

    // Then
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  public void should_not_be_equivalent_with_different_index_value() {
    // Given / When
    final ReadOperation first = new ReadOperation(INDEX_1);
    final ReadOperation second = new ReadOperation(INDEX_2);

    // Then
    assertNotEquals(first, second);
    assertNotEquals(first.hashCode(), second.hashCode());
  }
}
