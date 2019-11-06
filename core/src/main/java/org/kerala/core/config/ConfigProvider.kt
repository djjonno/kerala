package org.kerala.core.config

import org.kerala.core.Environment

object ConfigProvider {
  val config: Config by lazy {
    compileConfig(Environment.args)
  }

  private fun compileConfig(args: Array<String>): Config {
    val config = load(listOf(
        /* Configuration `Source`s in order of ascending precedence. */
        ConfigDefaultsSource(),
        ConfigPropertiesFileSource(),
        ConfigCliSource(args)
    ))
    return Config(config)
  }

  private fun load(sources: List<Source>): Map<String, String> {
    return sources.map { it.compile() }.reduce { acc, map ->
      (acc as MutableMap).putAll(map)
      acc
    }
  }
}
