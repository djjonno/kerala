package org.kerala.core

import org.kerala.core.config.Config
import org.kerala.core.config.ConfigProvider
import org.kerala.shared.io.File

/* Singleton */
object Environment {
  const val DEFAULT_HOME = "/usr/local/kerala"
  const val HOME_VAR = "KERALA_HOME"

  var args: Array<String> = emptyArray()
  val config: Config by lazy { ConfigProvider.config }

  private val home: String = this[HOME_VAR] ?: DEFAULT_HOME

  fun getHomePath(filePath: String): String {
    return File.join(home, filePath)
  }

  operator fun get(name: String): String? {
    return System.getenv(name)
  }
}
