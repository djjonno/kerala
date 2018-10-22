package org.elkd.core.config;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ConfigTest {
  private static final String KEY_1 = "key1";
  private static final String VALUE_1 = "value1";
  private static final Map<String, String> CONFIG = ImmutableMap.of(
      KEY_1, VALUE_1
  );

  @Test
  public void should_return_defaults() {
    // Given / When
    final Map<String, String> keyDefaults = Config.getKeyDefaults();

    // Then
    assertTrue(keyDefaults.containsKey(Config.KEY_DATA_DIR));
    assertFalse(keyDefaults.get(Config.KEY_DATA_DIR).isEmpty());
  }

  @Test
  public void should_return_value_of_key() {
    // Given / When
    final Config config = new Config(CONFIG);

    // Then
    assertEquals(VALUE_1, config.get(KEY_1));
  }
}
