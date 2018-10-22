package org.elkd.core.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigProvider {
  private ConfigProvider() { }

  public static Config getConfig() {
    final Map<String, String> config = load(ImmutableList.of(
        new ConfigDefaultsSource(),
        new ConfigPropertiesFileSource()
    ));
    return new Config(ImmutableMap.copyOf(config));
  }

  @VisibleForTesting
  static Config getConfig(final List<Source> sources) {
    return new Config(ImmutableMap.copyOf(load(sources)));
  }

  private static Map<String, String> load(final List<Source> sources) {
    final Map<String, String> compiledConfig = new HashMap<>();
    for (final Source source : sources) {
      compiledConfig.putAll(source.apply(compiledConfig));
    }
    return compiledConfig;
  }
}
