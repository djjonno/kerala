package org.elkd.core.config;

import java.util.Map;

class ConfigDefaultsSource implements Source {
  @Override
  public Map<String, String> apply(final Map<String, String> map) {
    return Config.getKeyDefaults();
  }
}
