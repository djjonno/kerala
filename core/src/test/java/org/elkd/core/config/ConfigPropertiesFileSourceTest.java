package org.elkd.core.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.elkd.core.Environment;
import org.elkd.core.config.ConfigPropertiesFileSource.InputStreamProvider;
import org.elkd.shared.io.File;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.elkd.core.config.ConfigPropertiesFileSource.ELKD_CONFIG_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class ConfigPropertiesFileSourceTest {
  private static final String ELKD_DEFAULT_HOME = "/elkd/home";
  private static final String KEY_1 = "key1";
  private static final String KEY_2 = "key2";
  private static final String VALUE_1 = "value1";
  private static final String VALUE_2 = "value2";
  private static final List<String> CONFIG_KEYS = ImmutableList.of(
      KEY_1,
      KEY_2
  );

  @Mock Environment mEnvironment;
  @Mock InputStream mInputStream;
  @Mock Properties mProperties;
  @Mock InputStreamProvider mInputStreamProvider;

  private ConfigPropertiesFileSource mUnitUnderTest;
  private String mFilePath;
  private HashMap<String, String> mConfig;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    setupCommonExpectations();
  }

  private void setupCommonExpectations() throws FileNotFoundException {
    mUnitUnderTest = new ConfigPropertiesFileSource();
    mConfig = new HashMap<>();

    doReturn(VALUE_1)
        .when(mProperties)
        .getProperty(KEY_1);
    doReturn(VALUE_2)
        .when(mProperties)
        .getProperty(KEY_2);

    setupFileEnvironment(ELKD_DEFAULT_HOME, ELKD_CONFIG_NAME);

    mUnitUnderTest = getUnit();
  }

  private void setupFileEnvironment(final String elkdHome, final String fileName) throws FileNotFoundException {
    mFilePath = File.join(elkdHome, fileName);

    doReturn(elkdHome)
        .when(mEnvironment)
        .getHome();

    doReturn(mFilePath)
        .when(mEnvironment)
        .getHomeFilePath(ELKD_CONFIG_NAME);

    doReturn(mInputStream)
        .when(mInputStreamProvider)
        .getStream(mFilePath);
  }

  private ConfigPropertiesFileSource getUnit() {
    return new ConfigPropertiesFileSource(
        mEnvironment,
        mInputStreamProvider,
        mProperties,
        CONFIG_KEYS);
  }

  @Test
  public void should_get_stream_with_path() throws FileNotFoundException {
    // Given / When
    mUnitUnderTest.apply(mConfig);

    // Then
    verify(mInputStreamProvider).getStream(mFilePath);
  }

  @Test
  public void should_get_stream_with_elkd_home() throws FileNotFoundException {
    // Given
    setupFileEnvironment("/new/home", ELKD_CONFIG_NAME);
    mUnitUnderTest = getUnit();

    // When
    mUnitUnderTest.apply(mConfig);

    // Then
    verify(mInputStreamProvider).getStream(mFilePath);
  }

  @Test
  public void should_load_properties_with_input_stream() throws IOException {
    // Given / When
    mUnitUnderTest.apply(mConfig);

    // Then
    verify(mProperties).load(mInputStream);
  }

  @Test
  public void should_load_properties_with_keys_only() {
    // Given / When
    mUnitUnderTest.apply(mConfig);

    // Then
    verify(mProperties).getProperty(KEY_1);
    verify(mProperties).getProperty(KEY_2);
    assertEquals(ImmutableMap.copyOf(mConfig), ImmutableMap.of(
        KEY_1, VALUE_1,
        KEY_2, VALUE_2
    ));
  }

  @Test
  public void should_not_load_null_properties() {
    // Given
    doReturn(null)
        .when(mProperties)
        .getProperty(KEY_2);

    // When
    mUnitUnderTest.apply(mConfig);

    // Then
    verify(mProperties).getProperty(KEY_1);
    verify(mProperties).getProperty(KEY_2);
    assertTrue(mConfig.size() == 1);
    assertEquals(mConfig.get(KEY_1), VALUE_1);
  }

  @Test
  public void should_override_prior_properties() {
    // Given
    mConfig.put(KEY_1, VALUE_1);
    final String expected = "override";
    doReturn(expected)
        .when(mProperties)
        .getProperty(KEY_1);

    // When
    mUnitUnderTest.apply(mConfig);

    // Then
    assertEquals(expected, mConfig.get(KEY_1));
  }

  @Test
  public void should_return_compiled_config() {
    // Given / When
    final Map<String, String> config = mUnitUnderTest.apply(mConfig);

    // Then
    assertEquals(VALUE_1, config.get(KEY_1));
    assertEquals(VALUE_2, config.get(KEY_2));
  }
}
