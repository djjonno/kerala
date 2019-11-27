package org.kerala.core.config

import com.google.common.annotations.VisibleForTesting
import org.kerala.core.Environment
import org.kerala.shared.logger
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
      logger {
        e("using default $CONFIG_NAME")
        d(e.message)
      }
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
  }
}
