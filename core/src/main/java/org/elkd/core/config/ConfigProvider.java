package org.elkd.core.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigProvider {
  public static Config getConfig() {
    final Map<String, String> config = new ConfigProvider(ImmutableList.of(
        new ConfigDefaultsSource(),
        new ConfigPropertiesFileSource()
    )).mConfig;

    return new Config(ImmutableMap.copyOf(config));
  }

  private final Map<String, String> mConfig;

  ConfigProvider(final List<Source> sources) {
    mConfig = load(sources);
  }

  private Map<String, String> load(final List<Source> sources) {
    final Map<String, String> compiledConfig = new HashMap<>();
    for (final Source source : sources) {
      compiledConfig.putAll(source.apply(compiledConfig));
    }
    return compiledConfig;
  }
}
