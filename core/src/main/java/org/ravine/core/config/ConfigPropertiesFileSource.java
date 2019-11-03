package org.ravine.core.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.ravine.core.Environment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

class ConfigPropertiesFileSource implements Source {
  @VisibleForTesting static final String CONFIG_NAME = "ravine.properties";
  private static final Logger LOG = Logger.getLogger(ConfigPropertiesFileSource.class);

  private final String mConfigFilePath;
  private final InputStreamProvider mInputStreamProvider;
  private Properties mProperties;
  private final List<String> mKeys;

  ConfigPropertiesFileSource() {
    this(Environment.getInstance(), new InputStreamProvider(), new Properties(), Config.getSupportedKeys());
  }

  @VisibleForTesting
  ConfigPropertiesFileSource(final Environment environment,
                             final InputStreamProvider inputStreamProvider,
                             final Properties properties,
                             final List<String> keys) {
    mInputStreamProvider = Preconditions.checkNotNull(inputStreamProvider, "inputStreamProvider");
    mProperties = Preconditions.checkNotNull(properties, "properties");
    mKeys = Preconditions.checkNotNull(keys, "keys");

    Preconditions.checkNotNull(environment, "environment");
    mConfigFilePath = environment.getHomeFilePath(CONFIG_NAME);
  }

  @Override
  public Map<String, String> apply(final Map<String, String> map) {
    try (InputStream input = mInputStreamProvider.getStream(mConfigFilePath)) {
      mProperties.load(input);
      for (final String key : mKeys) {
        final String value = mProperties.getProperty(key);
        if (value != null) {
          map.put(key, value);
        }
      }
    } catch (final Exception e) {
      LOG.error("using default ravine.properties");
      LOG.debug(e.getMessage());
    }

    return map;
  }

  static class InputStreamProvider {
    InputStream getStream(final String filePath) throws FileNotFoundException {
      return new FileInputStream(filePath);
    }
  }
}
