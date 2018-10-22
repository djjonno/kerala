package org.elkd.core;

import com.google.common.annotations.VisibleForTesting;
import org.elkd.shared.io.File;

/* Singleton */
public class Environment {
  private static final String ELKD_DEFAULT_HOME = "/usr/local/elkd";
  private static final String ELKD_HOME_VAR = "ELKD_HOME";

  private static Environment instance = null;

  public static Environment getInstance() {
    synchronized (Environment.class) {
      if (instance == null) {
        instance = new Environment();
      }
      return instance;
    }
  }

  private Environment() { }

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
