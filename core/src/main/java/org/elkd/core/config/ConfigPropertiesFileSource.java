package org.elkd.core.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.elkd.core.Environment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

class ConfigPropertiesFileSource implements Source {
  @VisibleForTesting static final String ELKD_CONFIG_NAME = "elkd.properties";
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
    mConfigFilePath = environment.getHomeFilePath(ELKD_CONFIG_NAME);
  }

  @Override
  public Map<String, String> apply(final Map<String, String> map) {
    try (final InputStream input = mInputStreamProvider.getStream(mConfigFilePath)) {
      mProperties.load(input);
      for (final String key : mKeys) {
        final String value = mProperties.getProperty(key);
        if (value != null) {
          map.put(key, value);
        }
      }
    } catch (final Exception e) {
      LOG.error("Failed to load elkd.properties, using defaults.");
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
