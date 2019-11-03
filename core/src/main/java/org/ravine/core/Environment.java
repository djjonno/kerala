package org.ravine.core;

import com.google.common.annotations.VisibleForTesting;
import org.ravine.shared.io.File;

/* Singleton */
public class Environment {
  @VisibleForTesting static final String DEFAULT_HOME = "/usr/local/ravine";
  @VisibleForTesting static final String HOME_VAR = "RAVINE_HOME";

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
    final String value = getEnv(HOME_VAR);
    return value != null
        ? value
        : DEFAULT_HOME;
  }

  public String getHomeFilePath(final String fileName) {
    return File.join(getHome(), fileName);
  }

  public String getEnv(final String name) {
    return System.getenv(name);
  }
}
