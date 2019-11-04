package org.ravine.core

import org.ravine.core.config.Config
import org.ravine.core.config.ConfigProvider
import org.ravine.shared.io.File

/* Singleton */
object Environment {
  const val DEFAULT_HOME = "/usr/local/ravine"
  const val HOME_VAR = "RAVINE_HOME"

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
