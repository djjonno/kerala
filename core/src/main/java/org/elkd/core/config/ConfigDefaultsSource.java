package org.elkd.core.config;

import org.apache.log4j.Logger;

import java.util.Map;

class ConfigDefaultsSource implements Source {
  private static final Logger LOG = Logger.getLogger(ConfigDefaultsSource.class);
  @Override
  public Map<String, String> apply(final Map<String, String> map) {
    try {
      return Config.getKeyDefaults();
    } catch (final IllegalAccessException e) {
      LOG.error("Could not load default config - server may fail to boot", e);
      return map;
    }
  }
}
