package org.elkd.core.statemachine;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InMemoryStateMachineTest {
  private static final String KEY = "key";
  private static final String VAL = "val";

  private InMemoryStateMachine mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    mUnitUnderTest = new InMemoryStateMachine();
  }

  @Test
  public void should_set_key_value() {
    // Given / When
    mUnitUnderTest.set(KEY, VAL);

    // Then
    assertEquals(VAL, mUnitUnderTest.get(KEY));
  }

  @Test
  public void should_unset_key() {
    // Given
    mUnitUnderTest.set(KEY, VAL);

    // When
    mUnitUnderTest.unset(KEY);

    // Then
    assertNull(mUnitUnderTest.get(KEY));
  }
}
