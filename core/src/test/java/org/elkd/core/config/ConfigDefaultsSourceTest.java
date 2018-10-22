package org.elkd.core.config;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ConfigDefaultsSourceTest {

  private ConfigDefaultsSource mUnitUnderTest;

  @Before
  public void setUp() throws Exception {
    mUnitUnderTest = new ConfigDefaultsSource();
  }

  @Test
  public void should_return_defaults() throws IllegalAccessException {
    // Given
    final Map<String, String> expected = Config.getKeyDefaults();

    // When
    mUnitUnderTest = new ConfigDefaultsSource();
    final Map<String, String> defaults = mUnitUnderTest.apply(new HashMap<>());

    // Then
    assertFalse(expected.isEmpty());
    assertEquals(expected, defaults);
  }
}
