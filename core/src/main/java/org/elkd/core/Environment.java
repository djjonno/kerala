package org.elkd.core;

import com.google.common.annotations.VisibleForTesting;
import org.elkd.shared.io.File;

/* Singleton */
public class Environment {
  @VisibleForTesting static final String ELKD_DEFAULT_HOME = "/usr/local/elkd";
  @VisibleForTesting static final String ELKD_HOME_VAR = "ELKD_HOME";

  private static Environment mInstance;

  private Environment() { }

  public static Environment getInstance() {
    synchronized (Environment.class) {
      if (mInstance == null) {
        mInstance = new Environment();
      }
      return mInstance;
    }
  }

  public String getHome() {
    final String value = getEnv(ELKD_HOME_VAR);
    return value != null
        ? value
        : ELKD_DEFAULT_HOME;
  }

  public String getHomeFilePath(final String fileName) {
    return File.join(getHome(), fileName);
  }

  public String getEnv(final String name) {
    return System.getenv(name);
  }
}
