package org.elkd.core.server.converters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class ConverterRegistryTest {

  @Mock Converter mConverter;

  private ConverterRegistry mUnitUnderTest;

  private final Source mSource = new Source();
  private final Target mTarget = new Target();

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    doReturn(ImmutableSet.of(Source.class))
        .when(mConverter)
        .forTypes();

    doReturn(mTarget)
        .when(mConverter)
        .convert(eq(mSource), any());

    mUnitUnderTest = new ConverterRegistry(ImmutableList.of(
        mConverter
    ));
  }

  @Test
  public void should_transform_object() {
    // Given / When
    final Object target = mUnitUnderTest.convert(mSource);

    // Then
    assertSame(mTarget, target);
    verify(mConverter).convert(eq(mSource), any());
  }

  @Test(expected = ConverterNotFoundException.class)
  public void should_throw_exception_when_no_adapter_found() {
    // Given
    mUnitUnderTest = new ConverterRegistry(ImmutableList.of());

    // When
    mUnitUnderTest.convert(mSource);

    // Then - exception thrown
  }

  @Test(expected = ConverterException.class)
  public void should_throw_converter_exception() {
    // Given
    doThrow(new ConverterException("failed to convert"))
        .when(mConverter)
        .convert(eq(mSource), any());

    // When
    mUnitUnderTest.convert(mSource);

    // Then - exception thrown
  }

  private static class Source { }
  private static class Target { }
}
