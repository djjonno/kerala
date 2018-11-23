package org.elkd.core.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class ConfigProviderTest {
  private static final String KEY_1 = "key1";
  private static final String KEY_2 = "key2";
  private static final String VALUE_1 = "value1";
  private static final String VALUE_2 = "value2";

  @Mock Source mSource1;
  @Mock Source mSource2;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    doReturn(ImmutableMap.of(
        KEY_1, VALUE_1
    )).when(mSource1).apply(any());
    doReturn(ImmutableMap.of(
        KEY_2, VALUE_2
    )).when(mSource2).apply(any());
  }

  private ImmutableList<Source> getSources() {
    return ImmutableList.of(
        mSource1,
        mSource2
    );
  }

  @Test
  public void should_load_all_sources_in_order() {
    // Given / When
    ConfigProvider.compileConfig(getSources());

    // Then
    final InOrder inOrder = Mockito.inOrder(mSource1, mSource2);
    inOrder.verify(mSource1).apply(any());
    inOrder.verify(mSource2).apply(any());
  }

  @Test
  public void should_combine_config_from_sources() {
    // Given / When
    final Config config = ConfigProvider.compileConfig(getSources());

    // Then
    assertEquals(VALUE_1, config.get(KEY_1));
    assertEquals(VALUE_2, config.get(KEY_2));
  }

  @Test
  public void should_override_config_values_in_source_order() {
    // Given
    final String first = "first";
    final String second = "second";
    doReturn(ImmutableMap.of(
        KEY_1, first
    )).when(mSource1).apply(any());
    doReturn(ImmutableMap.of(
        KEY_1, second
    )).when(mSource2).apply(any());

    // When
    final Config config = ConfigProvider.compileConfig(getSources());

    // Then
    assertEquals(second, config.get(KEY_1));
  }
}
