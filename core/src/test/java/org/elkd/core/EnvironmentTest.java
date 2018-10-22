package org.elkd.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class EnvironmentTest {

  @Test
  public void should_use_ELKD_HOME_env_var() {
    assertEquals("ELKD_HOME", Environment.ELKD_HOME_VAR);
  }

  @Test
  public void should_return_default_home() {
    // Given / When
    final String home = Environment.getInstance().getHome();

    assertEquals(Environment.ELKD_DEFAULT_HOME, home);
  }

  @Test
  public void should_return_valid_file_path() {
    // Given
    final String fileName = "elkd.properties";
    final String expected = Environment.ELKD_DEFAULT_HOME + "/" + fileName;

    // When
    final String path = Environment.getInstance().getHomeFilePath(fileName);

    // Then
    assertEquals(expected, path);
  }
}
