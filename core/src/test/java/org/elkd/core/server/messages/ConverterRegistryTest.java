package org.elkd.core.server.messages;

import com.google.common.collect.ImmutableMap;
import org.elkd.core.server.messages.exceptions.ConverterException;
import org.elkd.core.server.messages.exceptions.ConverterNotFoundException;
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

    mUnitUnderTest = new ConverterRegistry(ImmutableMap.of(
        Source.class, mConverter
    ));

    doReturn(mTarget)
        .when(mConverter)
        .convert(mSource);
  }

  @Test
  public void should_transform_object() {
    // Given / When
    final Object target = mUnitUnderTest.transform(mSource);

    // Then
    assertSame(mTarget, target);
    verify(mConverter).convert(mSource);
  }

  @Test(expected = ConverterNotFoundException.class)
  public void should_throw_exception_when_no_adapter_found() {
    // Given
    mUnitUnderTest = new ConverterRegistry(ImmutableMap.of());

    // When
    mUnitUnderTest.transform(mSource);


    // Then - exception thrown
  }

  @Test(expected = ConverterException.class)
  public void should_throw_converter_exception() {
    // Given
    doThrow(new ConverterException("failed to convert"))
        .when(mConverter)
        .convert(mSource);

    // When
    mUnitUnderTest.transform(mSource);

    // Then - exception thrown
  }

  private static class Source { }
  private static class Target { }
}
