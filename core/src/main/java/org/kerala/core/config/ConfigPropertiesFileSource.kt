package org.kerala.core.config

import com.google.common.annotations.VisibleForTesting
import org.apache.log4j.Logger
import org.kerala.core.Environment
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.Properties

internal class ConfigPropertiesFileSource @VisibleForTesting
constructor(private val configFilePath: String = Environment.getHomePath(CONFIG_NAME),
            private val inputStreamProvider: InputStreamProvider = InputStreamProvider(),
            private val properties: Properties = Properties(),
            private val keys: Set<String> = Config.supportedKeys) : Source {

  override fun compile(): Map<String, String> {
    return try {
      inputStreamProvider.getStream(configFilePath).use { input ->
        properties.load(input)
        keys.map {
          it to properties[it].toString()
        }.toMap()
      }
    } catch (e: Exception) {
      LOG.error("using default $CONFIG_NAME")
      LOG.debug(e.message)
      emptyMap()
    }
  }

  internal class InputStreamProvider {
    @Throws(FileNotFoundException::class)
    fun getStream(filePath: String): InputStream {
      return FileInputStream(filePath)
    }
  }

  companion object {
    const val CONFIG_NAME = "kerala.properties"
    private val LOG = Logger.getLogger(ConfigPropertiesFileSource::class.java)
  }
}
