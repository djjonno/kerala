package org.elkd.core.config;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigCLISourceTest {

  private static final Option CLUSTER_SET = Option.builder()
      .required(false)
      .desc("nodes to be included in your static cluster.")
      .longOpt("cluster-set")
      .numberOfArgs(1)
      .build();
  private static final String ARG_1 = "--cluster-set";
  private static final String KEY_1 = "cluster.set";
  private static final String VALUE_1 = "elkd://localhost:9191,elkd://localhost:9192";
  private static final String VALUE_2 = "elkd://0.0.0.0:9191,elkd://0.0.0.0:9192";
  private static final String[] ARGS = {
      ARG_1, VALUE_1
  };

  private ConfigCLISource mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    mUnitUnderTest = new ConfigCLISource(ARGS, new Option[]{ CLUSTER_SET });
  }

  @Test
  public void should_parse_cli_args() throws Exception {
    // Given / When
    final Map<String, String> config = mUnitUnderTest.apply(new HashMap<>());

    // Then
    assertEquals(config.get(KEY_1), VALUE_1);
  }

  @Test
  public void should_override_config() {
    // Given
    final Map<String, String> oldConfig = new HashMap<String, String>() {{
      put(KEY_1, VALUE_2);
    }};

    // When
    final Map<String, String> newConfig = mUnitUnderTest.apply(oldConfig);

    // Then
    assertEquals(newConfig.get(KEY_1), VALUE_1);
  }

  @Test(expected = Exception.class)
  public void should_throw_exception_when_args_contains_help() throws Exception {
    // Given / When
    mUnitUnderTest = new ConfigCLISource(new String[] {
        "--help"
    });

    // Then - exception thrown
  }

  @Test
  public void should_strip_all_hyphens() throws Exception {
    // Given
    mUnitUnderTest = new ConfigCLISource(new String[] {
        "--cluster-set", "elkd://0.0.0.0:9191"
    });

    // When
    final Map<String, String> config = mUnitUnderTest.apply(new HashMap<>());

    // Then
    assertTrue(config.containsKey(KEY_1));
  }

  @Test(expected = MissingArgumentException.class)
  public void should_throw_exception_when_invalid_arg() throws Exception {
    // Given / When
    mUnitUnderTest = new ConfigCLISource(new String[] {
        ARG_1
    });

    // Then - exception thrown
  }
}
