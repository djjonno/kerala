package org.elkd.core.config;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

  /**
   * Data storage location.
   */
  @Key(defaultValue = "/usr/local/elkd") public static final String KEY_DATA_DIR = "data.dir";

  private static final Logger LOG = Logger.getLogger(Config.class);
  private final Map<String, String> mConfig;

  Config(final Map<String, String> config) {
    mConfig = config;
  }

  public String get(final String key) {
    return mConfig.get(key);
  }

  /* Helpers */

  static List<String> getSupportedKeys() {
    final ArrayList<String> keys = new ArrayList<>();
    for (final Field field : Config.class.getDeclaredFields()) {
      if (field.isAnnotationPresent(Key.class)) {
        try {
          keys.add((String) field.get(null));
        } catch (final IllegalAccessException e) {
          LOG.error("wtf - could not access field", e);
        }
      }
    }
    return keys;
  }

  static Map<String, String> getKeyDefaults() {
    final Map<String, String> defaultConfig = new HashMap<>();
    for (Field field : Config.class.getDeclaredFields()) {
      if (field.isAnnotationPresent(Key.class)) {
        try {
          defaultConfig.put((String) field.get(null), field.getAnnotation(Key.class).defaultValue());
        } catch (IllegalAccessException e) {
          LOG.error("wtf - could not access field", e);
        }
      }
    }
    return defaultConfig;
  }
}
