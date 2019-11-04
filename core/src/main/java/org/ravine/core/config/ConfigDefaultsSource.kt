package org.ravine.core.config

internal class ConfigDefaultsSource : Source {
  override fun compile(): Map<String, String> = Config.keyDefaults
}
