package org.elkd.client.lib;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestModelTest {

  private static final String NAME = "testName";
  private TestModel mUnitUnderTest;

  @Test
  public void should_return_expected_name() {
    // Given / When
    mUnitUnderTest = new TestModel(NAME);


    // Then
    assertEquals(NAME, mUnitUnderTest.getName());
  }
}